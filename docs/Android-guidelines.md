# 现代 Android 开发指南: Crypto.com 钱包仪表盘

本文档为 Crypto.com Android 钱包仪表盘项目提供了一套开发指南和最佳实践，旨在确保代码质量、一致性和可维护性。

## 1. 项目结构 (Clean Architecture)

遵循 Clean Architecture 原则，将项目代码划分为以下主要模块/层级：

*   **`app` (应用模块)**: 包含 Android 框架相关的代码，连接各层。
    *   **`di`**: 依赖注入配置 (Hilt 模块)。
    *   **`presentation`**: UI 层，包含 Activities, Fragments, ViewModels, Adapters, XML 布局。
    *   **`CryptoWalletApp.kt`**: Application 类 (用于 Hilt 初始化等)。
*   **`domain` (领域层)**: 包含核心业务逻辑和模型，独立于 Android 框架。
    *   **`model`**: 领域实体 (Data classes，如 `Currency`, `AggregatedBalance`)。
    *   **`repository`**: Repository 接口定义。
    *   **`usecase`**: 业务用例/交互器。
    *   **`common`**: 领域层通用的辅助类 (如 `Result` wrapper)。
*   **`data` (数据层)**: 负责数据获取和管理。
    *   **`datasource`**: 数据源定义 (本地 JSON, 未来可能是 Remote API, Local DB)。
        *   `local`: 本地数据源实现 (如 JSON 文件读取)。
        *   `remote`: (未来) 网络数据源接口及实现。
    *   **`mapper`**: 数据模型转换器 (DTO <-> Domain Model)。
    *   **`model`**: 数据层模型 (DTOs - Data Transfer Objects)，如果与领域模型不同。
    *   **`repository`**: Repository 接口的实现。

**注意**: 对于此规模的项目，`domain` 和 `data` 也可以是 `app` 模块内的 package，而非独立的 Gradle 模块，但逻辑分层必须清晰。

## 2. 命名规范

*   **类与接口**: `PascalCase` (e.g., `DashboardViewModel`, `CurrencyRepository`)。
*   **函数与变量**: `camelCase` (e.g., `loadBalances`, `isLoading`)。
*   **常量 (const val)**: `UPPER_SNAKE_CASE` (e.g., `DEFAULT_TIMEOUT`)。
*   **Layout XML 文件**: `activity_*.xml`, `fragment_*.xml`, `item_*.xml`, `view_*.xml` (e.g., `activity_dashboard.xml`, `item_balance.xml`)。
*   **Drawable/Mipmap XML 文件**: `ic_*` (图标), `bg_*` (背景), `shape_*` (形状) (e.g., `ic_btc.xml`, `bg_card.xml`)。
*   **XML View IDs**: `snake_case` (e.g., `text_view_currency_name`, `button_retry`)。
*   **ViewModel 中的 LiveData/Flow**: 后缀 `Flow` 或 `State` (e.g., `balancesFlow: StateFlow<...>`).
*   **UseCase 类**: 后缀 `UseCase` (e.g., `GetWalletBalancesUseCase`)。

## 3. 编码风格与 Kotlin

*   **遵循 Kotlin 官方编码风格指南**: [https://kotlinlang.org/docs/coding-conventions.html](https://kotlinlang.org/docs/coding-conventions.html)
*   **类型明确**: 明确声明变量和函数返回类型，避免使用 `Any`。
*   **不可变性优先**: 尽可能使用 `val` 和不可变集合 (`List`, `Set`, `Map`)。
*   **作用域函数**: 谨慎、合理地使用 `let`, `run`, `with`, `apply`, `also`，以提高可读性为准。
*   **表达式函数体**: 对于简单的单行函数，使用表达式函数体。
*   **KDoc**: 为所有公共 API (类、方法、函数) 编写 KDoc 注释。

## 4. 架构实践 (MVVM + Clean Architecture)

*   **View (Activity/Fragment)**: 职责仅限于 UI 渲染和用户输入转发。应尽可能"哑"（Dumb）。通过 ViewBinding 访问视图。观察 ViewModel 中的 StateFlow/LiveData。
*   **ViewModel**: 负责准备和管理 UI 相关的数据（UI State）。持有对 UseCase 的引用。调用 UseCase 获取数据，处理业务逻辑结果，并将最终状态暴露给 View。**不应**直接引用 Android Framework 类（Context 除外，通过 Hilt 注入 ApplicationContext）。使用 `viewModelScope` 启动协程。
*   **UseCase**: 封装单一的业务逻辑单元。组合一个或多个 Repository。应是轻量级的，并且通常只有一个公共方法（通常是 `invoke` 操作符）。
*   **Repository**: 数据访问的抽象层。定义数据操作接口（由 Domain 层定义）。实现类（在 Data 层）负责协调来自不同数据源（本地、远程）的数据。
*   **DataSource**: 负责与具体的数据来源（JSON 文件、网络 API、数据库）交互。
*   **Mapper**: 负责在 Data 层 DTO 和 Domain 层 Model 之间进行转换。
*   **数据流**: View -> ViewModel -> UseCase -> Repository -> DataSource。
*   **状态管理**: 使用 `StateFlow` 或 `SharedFlow` (根据场景选择) 在 ViewModel 中暴露 UI 状态。状态应封装在一个 Data Class 中，包含加载状态、数据、错误信息等。

## 5. UI 开发 (XML & Material Components)

*   **ViewBinding**: 必须启用并使用，禁止使用 `findViewById` 或 Kotlin Synthetics。
*   **布局**: 优先使用 `ConstraintLayout` 创建灵活、扁平的布局。避免过度嵌套。
*   **Material Components**: 使用 `com.google.android.material` 库中的组件 (e.g., `MaterialButton`, `CardView`, `TextInputLayout`) 以确保 UI 一致性和现代感。
*   **RecyclerView**: 使用 `ListAdapter` 和 `DiffUtil` 实现高效的列表更新。
*   **资源管理**: 硬编码字符串、尺寸、颜色必须提取到 `strings.xml`, `dimens.xml`, `colors.xml` 中。
*   **主题与样式**: 定义 AppTheme (继承自 Material 主题)，并使用 `styles.xml` 抽取可复用的视图属性。
*   **预览**: 积极使用 Android Studio 的布局预览功能，包括不同设备尺寸和主题。

## 6. 并发 (Coroutines & Flow)

*   **调度器 (Dispatchers)**: 明确指定协程运行的调度器。
    *   `Dispatchers.Main`: 用于 UI 更新。
    *   `Dispatchers.IO`: 用于磁盘或网络 I/O 操作。
    *   `Dispatchers.Default`: 用于 CPU 密集型计算。
*   **ViewModel Scope**: 在 ViewModel 中使用 `viewModelScope.launch` 启动与 UI 生命周期绑定的协程。
*   **Flow**: Repository 和 UseCase 应返回 `Flow`。ViewModel 收集 Flow 并将其转换为 UI State Flow。
*   **异常处理**: 在 Flow 中使用 `catch` 操作符处理异常，或在 `collect` 时使用 try-catch。
*   **结构化并发**: 确保遵循结构化并发原则，避免协程泄漏。

## 7. 依赖注入 (Hilt)

*   **注解**: 使用 `@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, `@Inject`, `@Module`, `@Provides`, `@Binds`。
*   **模块化**: 将依赖提供逻辑组织在 Hilt 模块中。
*   **作用域**: 合理使用 Hilt 提供的作用域 (e.g., `@Singleton`, `@ActivityScoped`, `@ViewModelScoped`)。
*   **构造函数注入**: 优先使用构造函数注入。

## 8. 错误处理

*   **Result Wrapper**: 推荐在 Repository 和 UseCase 的返回类型中使用 `Result` 封装类（如 `domain/common/Result.kt`），统一处理成功和失败情况。
*   **UI反馈**: ViewModel 应将错误状态映射到 UI State，View 根据状态显示用户友好的错误信息（如 Toast, Snackbar, 或界面内提示）。

## 9. 测试

*   **单元测试**: 覆盖 ViewModel、UseCase、Mapper 和 DataSource 中的逻辑。使用 JUnit 和 Mockito/Mockk。
*   **集成测试**: (可选) 测试层与层之间的交互，例如 ViewModel -> UseCase -> Repository。
*   **测试覆盖率**: 追求合理的测试覆盖率，尤其是针对核心业务逻辑。

## 10. Git 工作流与提交

*   **分支**: 建议使用简单的 Git Flow (e.g., `main`, `develop`, `feature/xxx`)。
*   **提交信息**: 遵循 Conventional Commits 规范 ([https://www.conventionalcommits.org/](https://www.conventionalcommits.org/))，编写清晰、有意义的提交信息 (e.g., `feat: add balance calculation`, `fix: correct json parsing error`, `refactor: improve repository implementation`)。
*   **持续提交**: 频繁进行小步、有意义的提交。

## 11. 文档

*   **KDoc**: 为所有公共类、方法和复杂逻辑编写清晰的 KDoc。
*   **README**: 保持 `README.md` 更新，包含项目概述、构建说明等。
 