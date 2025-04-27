# Crypto.com Android Wallet Dashboard Demo

## Overview

This project implements a Wallet Dashboard application that demonstrates modern Android development practices and architectural patterns. The application displays cryptocurrency balances and their corresponding USD values.

## Project Requirements

### Technical Stack
- **Language**: Kotlin
- **Architecture**: MVVM with Clean Architecture
- **Reactive Programming**: Kotlin Coroutine Flow
- **Version Control**: Git with continuous commits
- **Timeline**: 3 days

### Core Features
- Multi-currency support (BTC, ETH, CRO)
- Real-time currency conversion to USD
- Wallet balance display
- Reactive data updates

## Data Sources

The application uses three JSON data sources:

1. **Supported Currencies**
   - Source: [currencies-json.md](json/currencies-json.md)
   - Purpose: Lists all supported cryptocurrencies

2. **Exchange Rates**
   - Source: [live-rates-json.md](json/live-rates-json.md)
   - Purpose: Provides current exchange rates to USD
   - Example: For 0.0026 BTC at rate 9194.93 USD
   - Calculation: 0.0026 * 9194.93 = 23.906818 USD

3. **Wallet Balances**
   - Source: [wallet-balance-json.md](json/wallet-balance-json.md)
   - Purpose: Contains current wallet balances for each currency

## Development Guidelines

### Architecture Considerations
- Implement clean architecture principles
- Design for scalability and future feature additions
- Use dependency injection
- Follow SOLID principles

### Code Quality Requirements
- Handle edge cases and error scenarios
- Maintain clean project structure
- Remove unused code and resources
- Write unit tests for critical components
- Document key architectural decisions

### Best Practices
- Follow Kotlin coding conventions
- Implement proper error handling
- Use meaningful commit messages
- Structure code for maintainability

## Getting Started

1. Clone the repository
```bash
git clone [repository-url]
```

2. Open in Android Studio or Cursor IDE

3. Build and run the project
```bash
./gradlew build
```

## Project Structure

```
app/
├── data/           # Data layer (Repository, Data Sources)
├── domain/         # Business logic and models
├── presentation/   # UI layer (Activities, ViewModels)
├── di/             # Dependency injection
└── utils/          # Utility classes
```

## Testing

- Unit tests for business logic
- Integration tests for data flow
- UI tests for critical user journeys

## Submission

1. Create a private GitHub repository
2. Make regular, meaningful commits
3. Share repository access when complete

## Evaluation Criteria

- Code architecture and organization
- Implementation of reactive programming
- Error handling and edge cases
- Code quality and cleanliness
- Git commit history
- Project documentation

---

For any questions or clarifications, please reach out to the hiring team.