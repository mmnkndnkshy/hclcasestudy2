package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

    @Inject
    private WarehouseRepository warehouseRepository;

    @Inject
    private com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver locationResolver;

    @Override
    public List<Warehouse> listAllWarehousesUnits() {
        return warehouseRepository.getAll().stream().map(this::toWarehouseResponse).toList();
    }

    @Override
    public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {

        var location = locationResolver.resolveByIdentifier(data.getLocation());

        var existing = warehouseRepository.findByBusinessUnitCode(data.getBusinessUnitCode());
        if (existing != null && existing.archivedAt == null) {
            throw new IllegalArgumentException("Active warehouse already exists for this business unit");
        }

        long count =
                warehouseRepository.getAll().stream()
                        .filter(w -> w.location.equalsIgnoreCase(data.getLocation()) && w.archivedAt == null)
                        .count();

        if (count >= location.maxNumberOfWarehouses) {
            throw new IllegalArgumentException("Max warehouses reached for location");
        }

        int totalCapacity =
                warehouseRepository.getAll().stream()
                        .filter(w -> w.location.equalsIgnoreCase(data.getLocation()) && w.archivedAt == null)
                        .mapToInt(w -> w.capacity)
                        .sum();

        if (totalCapacity + data.getCapacity() > location.maxCapacity) {
            throw new IllegalArgumentException(
                    "Total warehouse capacity exceeds limit for location " + data.getLocation());
        }

        var warehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        warehouse.businessUnitCode = data.getBusinessUnitCode();
        warehouse.location = data.getLocation();
        warehouse.capacity = data.getCapacity();
        warehouse.stock = data.getStock();
        warehouse.createdAt = java.time.LocalDateTime.now();

        warehouseRepository.create(warehouse);

        return toWarehouseResponse(warehouse);
    }

    @Override
    public Warehouse getAWarehouseUnitByID(String id) {

        var warehouse = warehouseRepository.findByBusinessUnitCode(id);

        if (warehouse == null) {
            throw new IllegalArgumentException("Warehouse not found: " + id);
        }

        return toWarehouseResponse(warehouse);
    }

    @Override
    public void archiveAWarehouseUnitByID(String id) {

        var warehouse = warehouseRepository.findByBusinessUnitCode(id);

        if (warehouse == null || warehouse.archivedAt != null) {
            throw new IllegalArgumentException("Active warehouse not found");
        }

        warehouse.archivedAt = java.time.LocalDateTime.now();

        warehouseRepository.update(warehouse);
    }

    @Override
    public Warehouse replaceTheCurrentActiveWarehouse(
            String businessUnitCode, @NotNull Warehouse data) {

        var location = locationResolver.resolveByIdentifier(data.getLocation());

        var existing = warehouseRepository.findByBusinessUnitCode(businessUnitCode);

        if (existing == null || existing.archivedAt != null) {
            throw new IllegalArgumentException("Active warehouse not found");
        }

        if (data.getCapacity() < existing.stock) {
            throw new IllegalArgumentException("New warehouse cannot fit existing stock");
        }

        if (!data.getStock().equals(existing.stock)) {
            throw new IllegalArgumentException("Stock must match previous warehouse");
        }

        // archive old
        existing.archivedAt = java.time.LocalDateTime.now();
        warehouseRepository.update(existing);

        // create new
        var newWarehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        newWarehouse.businessUnitCode = businessUnitCode;
        newWarehouse.location = data.getLocation();
        newWarehouse.capacity = data.getCapacity();
        newWarehouse.stock = data.getStock();
        newWarehouse.createdAt = java.time.LocalDateTime.now();

        warehouseRepository.create(newWarehouse);

        return toWarehouseResponse(newWarehouse);
    }

    private Warehouse toWarehouseResponse(
            com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
        var response = new Warehouse();
        response.setBusinessUnitCode(warehouse.businessUnitCode);
        response.setLocation(warehouse.location);
        response.setCapacity(warehouse.capacity);
        response.setStock(warehouse.stock);

        return response;
    }
}
