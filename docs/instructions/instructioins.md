# Project Requirements: Crypto.com Android Wallet Dashboard Demo

## 1. Overview

Develop an Android Wallet Dashboard application demonstrating modern development practices. The application will display cryptocurrency balances and their corresponding USD values, adhering to the specified technical stack and features.

## 2. Technical Stack

*   **Language**: Kotlin
*   **Architecture**: MVVM combined with Clean Architecture principles.
*   **Reactive Programming**: Utilize Kotlin Coroutine Flow for handling asynchronous data streams and UI updates.
*   **Version Control**: Git, with a history of continuous, meaningful commits.
*   **Development Timeline**: 3 days.

## 3. Core Features

*   **Multi-currency Support**: Display balances for Bitcoin (BTC), Ethereum (ETH), and Cronos (CRO).
*   **Real-time Currency Conversion**: Convert displayed cryptocurrency balances to their equivalent USD value using current exchange rates.
*   **Wallet Balance Display**: Show the current balance for each supported cryptocurrency held in the wallet.
*   **Reactive Data Updates**: Implement reactive patterns so that UI updates automatically reflect changes in underlying data (e.g., balance changes, rate updates).

## 4. Data Sources

The application must integrate with the following data sources provided in JSON format:

*   **Supported Currencies**:
    *   Source: `json/currencies-json.md`
    *   Purpose: Provides a list of all cryptocurrencies the application should support.
*   **Exchange Rates**:
    *   Source: `json/live-rates-json.md`
    *   Purpose: Contains real-time exchange rates for converting supported cryptocurrencies to USD.
*   **Wallet Balances**:
    *   Source: `json/wallet-balance-json.md`
    *   Purpose: Lists the current quantity of each cryptocurrency held in the user's wallet.

## 5. Development Guidelines

### 5.1. Architecture

*   Strictly follow Clean Architecture principles to separate concerns (data, domain, presentation).
*   Implement the Model-View-ViewModel (MVVM) pattern for the presentation layer.
*   Design for scalability to accommodate potential future features easily.
*   Utilize Dependency Injection (DI) for managing dependencies throughout the application.
*   Adhere to SOLID principles in class design.

### 5.2. Code Quality

*   Implement robust handling for edge cases and potential error scenarios (e.g., network errors, invalid data).
*   Maintain a clean, logical, and well-organized project structure.
*   Ensure no unused code, imports, or resources remain in the final submission.
*   Write comprehensive unit tests for critical business logic components (e.g., calculation logic, data mapping).
*   Document key architectural decisions and complex logic where necessary.

### 5.3. Best Practices

*   Adhere to standard Kotlin coding conventions and style guides.
*   Implement proper error handling mechanisms (e.g., try-catch blocks, result types).
*   Use clear, descriptive, and meaningful Git commit messages.
*   Structure code for readability and long-term maintainability.

## 6. Testing Strategy

*   **Unit Tests**: Focus on testing individual components, particularly within the domain layer (business logic) and data layer (repositories, data mappers).
*   **Integration Tests**: Verify the interaction between different layers, such as the data flow from data sources through the repository to the ViewModel.
*   **UI Tests**: Implement tests for critical user journeys and UI interactions to ensure the presentation layer functions correctly.

## 7. Submission Guidelines

*   Create a private GitHub repository for the project.
*   Make regular commits with meaningful messages reflecting the work done.
*   Share access to the repository with the hiring team upon completion.

## 8. Evaluation Criteria

The project will be evaluated based on:

*   **Architecture**: Adherence to Clean Architecture and MVVM.
*   **Reactive Programming**: Correct and effective use of Kotlin Flow.
*   **Error Handling**: Robustness in handling errors and edge cases.
*   **Code Quality**: Cleanliness, organization, and maintainability of the codebase.
*   **Version Control**: Quality and frequency of Git commits.
*   **Documentation**: Clarity of project documentation and in-code comments where appropriate.
