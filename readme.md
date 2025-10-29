# CodeJudge - Online DSA Execution Platform

This project is a high-complexity system that moves beyond basic CRUD by implementing a decoupled, message-driven architecture to handle code compilation and execution in a secure, sandboxed environment (Docker containers). The core challenge lies in the secure and asynchronous processing of external code.

## I. Project Definition
### 1. Business Requirements (10)

1. **Code Sandboxing:** The platform **must** execute user-submitted code within an isolated, resource-limited Docker container to prevent malicious activity (e.g., file system access, network calls) or resource exhaustion
2. **Multilingual Support:** The system **must** accept and compile code submissions in at least three modern languages (e.g., Java, Python, C++).
3. **Asynchronous Processing:** Code submission and execution **must** be decoupled using a Message Queue. The API should return an immediate status and complete the execution task out-of-band.
4. **Performance Metrics:** The system **must** accurately measure and report the **execution time (in milliseconds)** and **memory consumption (in KB)** for each test case execution.
5. **Test Case Visibility:** Every problem **must** support both public (example) and private (hidden) test cases for comprehensive and fair evaluation.
6. **Submission History:** Users **must** be able to view the full details (submitted code, final status, performance metrics, and individual test case results) of all their past submissions.
7. **Time/Memory Limits:** The system **must** enforce problem-specific time and memory constraints, returning a dedicated **Time Limit Exceeded (TLE)** or **Memory Limit Exceeded (MLE)** status if violated.
8. **Admin Tools:** Administrators **must** have the capability to create, edit, and manage problems, difficulty levels, and the associated hidden test cases.
9. **Data Validation:** All critical input data (e.g., problem constraints, submitted code, language IDs) **must** be validated using server-side POJO validation.
10. **Progress Tracking:** The platform **must** track and store a user's submissions for every problem.


### 2. Minimum Viable Product (MVP) Features (5)


The MVP is designed around the core user workflow and the asynchronous execution architecture.

| MVP Feature                          | Core Business Logic / Service                                                                                                                                     | Rationale for Non-CRUD Complexity                                                                                                                   |
|:-------------------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------|
| **1. Problem Discovery & Detail**    | Users can browse and filter problems by topic/difficulty and view the full statement. (Managed by `ProblemService`)                                               | Requires complex query optimization, filtering, and optional user-specific data retrieval (e.g., showing if the user has solved it).                |
| **2. Code Submission API**           | Accepts raw code, language ID, and problem ID. **Saves the submission** and **sends an execution message** to the Message Queue. (Managed by `SubmissionService`) | **Transactionality & Asynchronous Orchestration:** Involves saving the initial state and triggering a decoupled process.                            |
| **3. Asynchronous Execution Status** | An endpoint that allows a user to poll for the final result of their submission. (Managed by `ResultService`)                                                     | **State Management:** Handles fetching the final result from the database once the **Dockerized execution worker** has completed and reported back. |
| **4. Submission History & Detail**   | Users can retrieve a list of all their attempts for a given problem or view the detailed results of a specific submission. (Managed by `SubmissionService`)       | Involves joining data across multiple entities (`Submission`, `SubmissionTestResult`, `Problem`) to present a comprehensive view.                   |
| **5. Admin Test Case Management**    | API endpoints for an admin role to upload/modify hidden test cases for a `Problem`. (Managed by `AdminService`)                                                   | **Authorization and Role Management:**  Requires Spring Security to enforce admin-only access and proper data sanitization.                         |