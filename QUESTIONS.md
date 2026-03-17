# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
In the current code base, there are mixed approaches for database interaction. For example, the `Warehouse` module follows a cleaner separation using a repository that implements a domain port (`WarehouseStore`), while other areas (like `Store`) directly rely on Panache entities.

If I were to maintain this code base, I would refactor towards a more consistent approach, preferably aligning everything with the Ports and Adapters (Hexagonal Architecture) pattern already used in the warehouse module. This means clearly separating domain logic from persistence concerns and accessing the database only through defined interfaces (ports).

The main benefits of this refactoring would be improved consistency, easier testability (by mocking ports), and better maintainability as the system grows. It also helps decouple business logic from specific frameworks like Panache or Hibernate.

That said, I would apply this refactoring incrementally rather than all at once, prioritizing areas with higher complexity or frequent changes, to avoid unnecessary risk and large refactoring overhead.

2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
Both approaches have their own advantages and trade-offs.

Using OpenAPI (contract-first) as seen in the Warehouse API provides a clear, standardized contract upfront. It improves collaboration between frontend, backend, and external teams, ensures consistency, and allows automatic code generation. This is particularly useful for public APIs or systems with multiple consumers.

On the other hand, the code-first approach used for `Store` and `Product` APIs is faster to develop initially and simpler for smaller or internal services. It gives developers more flexibility and reduces the overhead of maintaining an additional specification file.

However, it can lead to inconsistencies and makes it harder to enforce a strict API contract across teams.

My preference would be:

 * Use **OpenAPI (contract-first)** for external-facing or shared APIs where consistency and collaboration are important.
 * Use **code-first** for small, internal, or rapidly evolving services.

In this project, I would lean towards standardizing on OpenAPI for all APIs to maintain consistency, especially if the system is expected to scale or integrate with other services.

3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
Given time and resource constraints, I would prioritize testing based on business impact and risk.

First, I would focus on **unit tests for core business logic**, especially in the Warehouse domain, since it contains important validation rules (capacity limits, replacement logic, stock constraints). These tests are fast, reliable, and provide the most value.

Next, I would add **integration tests** for the repository layer to ensure correct interaction with the database, particularly for persistence and retrieval operations.

Then, I would include a few **API-level tests (using RestAssured or similar tools)** to validate critical endpoints end-to-end, such as warehouse creation and replacement flows.

To keep test coverage effective over time, I would:

* Focus on testing business rules rather than implementation details
* Add tests for any new feature or bug fix
* Keep tests maintainable and readable
* Integrate tests into CI/CD pipelines to ensure they run on every change

This approach ensures a good balance between coverage, speed, and maintainability without over-engineering the testing effort.
