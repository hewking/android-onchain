# 应用实现步骤: Crypto.com Android 钱包仪表盘

本文档基于项目需求、UI 设计（指定范围）、应用流程和技术栈，规划了详细的应用实现步骤。

**核心目标**: 实现 UI 图中红框标示的核心功能：显示总资产（USD）和包含 BTC, ETH, CRO（以及其他有余额的）货币列表（名称、余额、USD价值）。其他 UI 元素（如发送/接收按钮、底部导航）和极致的 UI 美观度不在本次任务范围内。

## 阶段一: 项目初始化与基础设置 (约 0.5 天)

1.  **创建项目**: 使用 Android Studio 创建一个新的 Kotlin 项目。
2.  **配置 Gradle (`build.gradle.kts`, `libs.versions.toml`)**:
    *   启用 ViewBinding。
    *   添加核心依赖:
        *   Kotlin & Coroutines (`kotlinx-coroutines-core`, `kotlinx-coroutines-android`)
        *   AndroidX Lifecycle (`lifecycle-viewmodel-ktx`, `lifecycle-runtime-ktx`)
        *   Hilt (`hilt-android`, `hilt-compiler`)
        *   Material Components (`com.google.android.material:material`)
        *   RecyclerView (`androidx.recyclerview:recyclerview`)
        *   ConstraintLayout (`androidx.constraintlayout:constraintlayout`)
        *   Kotlinx Serialization (`kotlinx-serialization-json`) - 需要配置插件。
3.  **配置 Hilt**:
    *   创建 Application 类 (`CryptoWalletApp.kt`) 并添加 `@HiltAndroidApp` 注解。
    *   在 `AndroidManifest.xml` 中声明 Application 类。
4.  **创建基础包结构**:
    *   `com.cryptocom.wallet.common` (通用工具/基类)
    *   `com.cryptocom.wallet.di` (Hilt 模块)
    *   `com.cryptocom.wallet.domain`
        *   `model`
        *   `repository`
        *   `usecase`
        *   `common` (Result wrapper)
    *   `com.cryptocom.wallet.data`
        *   `datasource.local`
        *   `mapper`
        *   `model` (DTOs)
        *   `repository` (Implementations)
    *   `com.cryptocom.wallet.presentation`
        *   `dashboard` (包含 Activity/Fragment, ViewModel, Adapter)
        *   `model` (UI State)
        *   `util` (UI 工具类，如格式化)
5.  **添加 JSON 数据文件**: 将 `currencies-json.md`, `live-rates-json.md`, `wallet-balance-json.md` 的 JSON 内容复制到 `app/src/main/assets/` 目录下，并重命名为 `.json` 文件 (e.g., `currencies.json`, `rates.json`, `balances.json`)。
6.  **版本控制**: 初始化 Git 仓库并进行首次提交。

## 阶段二: Domain 层定义 (约 0.5 天)

1.  **定义核心数据类 (`domain/model`)**:
    *   `Currency.kt`: `id: String`, `name: String`, `symbol: String`, `iconUrl: String?` (图标可选)
    *   `ExchangeRate.kt`: `fromSymbol: String`, `toSymbol: String`, `rate: BigDecimal`
    *   `WalletBalance.kt`: `currencySymbol: String`, `amount: BigDecimal`
    *   `AggregatedBalance.kt`: `currency: Currency`, `balanceAmount: BigDecimal`, `balanceUsdValue: BigDecimal`
    *   **`DashboardData.kt`**: (用于 UseCase 返回) `balances: List<AggregatedBalance>`, `totalUsdValue: BigDecimal`
2.  **定义 `Result` 封装类 (`domain/common`)**:
    *   `Result.kt`: `sealed class Result<out T>` (包含 `Success<T>` 和 `Error`)
3.  **定义 Repository 接口 (`domain/repository`)**:
    *   `CurrencyRepository.kt`: `fun getSupportedCurrencies(): Flow<Result<List<Currency>>>`
    *   `RateRepository.kt`: `fun getExchangeRates(): Flow<Result<List<ExchangeRate>>>`
    *   `WalletRepository.kt`: `fun getWalletBalances(): Flow<Result<List<WalletBalance>>>`
4.  **定义 Use Cases (`domain/usecase`)**:
    *   `GetAggregatedDashboardDataUseCase.kt`: `operator fun invoke(): Flow<Result<DashboardData>>` (内部组合调用三个 Repository，进行数据聚合和总值计算)

## 阶段三: Data 层实现 (约 1 天)

1.  **定义 DTOs (`data/model`)**:
    *   根据 `assets` 目录下 JSON 文件的实际结构定义对应的 Data Transfer Objects (使用 `@Serializable` 注解)。例如：
        *   `CurrencyDto.kt`
        *   `RateDto.kt`
        *   `BalanceDto.kt`
2.  **实现数据映射 (`data/mapper`)**:
    *   创建 Mapper 类或扩展函数，用于 DTOs 和 Domain Models 之间的转换 (e.g., `CurrencyMapper.kt`, `RateMapper.kt`, `BalanceMapper.kt`)。处理数据格式转换（如 String 转 BigDecimal）和错误。
3.  **实现本地数据源 (`data/datasource/local`)**:
    *   `LocalJsonDataSource.kt` (或分拆为多个 DataSource):
        *   注入 `Context` (通过 Hilt)。
        *   提供函数从 `AssetManager` 读取 JSON 文件内容 (`readJsonFromAssets(fileName: String): String?`)。
        *   提供函数解析各种 JSON (`parseCurrenciesJson`, `parseRatesJson`, `parseBalancesJson`)，使用 `kotlinx.serialization.json.Json`，返回 `List<DtoType>`。包含错误处理。
    *   `CurrencyLocalDataSource.kt`, `RateLocalDataSource.kt`, `WalletLocalDataSource.kt` (接口和实现)，封装 JSON 读取和解析逻辑，返回 `Flow<Result<List<DtoType>>>` 或直接 `List<DtoType>` (错误处理可以在 Repository 做)。
4.  **实现 Repository (`data/repository`)**:
    *   `CurrencyRepositoryImpl.kt`, `RateRepositoryImpl.kt`, `WalletRepositoryImpl.kt`:
        *   实现 Domain 层定义的 Repository 接口。
        *   注入对应的 LocalDataSource 和 Mapper。
        *   调用 DataSource 获取 DTO 数据，使用 Flow 操作符 (如 `flow`, `map`, `catch`)。
        *   调用 Mapper 将 DTO 转换为 Domain Model。
        *   使用 `catch` 操作符捕获异常，并 `emit(Result.Error)`.
        *   返回 `Flow<Result<List<DomainModel>>>`。

## 阶段四: 依赖注入配置 (约 0.25 天)

1.  **创建 Hilt 模块 (`di`)**:
    *   `AppModule.kt`: 提供 `Context`, `AssetManager`, `Json` (kotlinx.serialization) 等应用级单例。
    *   `DataSourceModule.kt`: 提供 LocalDataSource 的实现。
    *   `RepositoryModule.kt`: 使用 `@Binds` 提供 Repository 接口的实现。
    *   `MapperModule.kt` (如果 Mapper 需要注入依赖)。

## 阶段五: Presentation 层 (UI) 实现 (约 1 天)

1.  **定义 UI State (`presentation/dashboard/model`)**:
    *   `DashboardUiState.kt`: `data class DashboardUiState(isLoading: Boolean = false, error: String? = null, totalBalance: BigDecimal = BigDecimal.ZERO, balances: List<AggregatedBalance> = emptyList())`
2.  **创建 ViewModel (`presentation/dashboard`)**:
    *   `DashboardViewModel.kt`:
        *   使用 `@HiltViewModel` 注解。
        *   注入 `GetAggregatedDashboardDataUseCase`。
        *   创建 `private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))`。
        *   创建 `val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()`。
        *   在 `init` 块中启动协程 (`viewModelScope.launch`) 调用 UseCase。
        *   收集 UseCase 返回的 `Flow<Result<DashboardData>>`。
        *   使用 `onStart { emit(Result.Loading) }` (如果 Result 定义了 Loading) 或在协程开始时更新 `_uiState` 为加载中。
        *   根据 `Result` 更新 `_uiState` (成功则更新数据和总额，失败则更新错误信息，加载状态相应改变)。
3.  **创建布局 XML (`res/layout`)**:
    *   `activity_dashboard.xml` (或 `fragment_dashboard.xml`):
        *   根布局 `ConstraintLayout`。
        *   `TextView` (`@+id/text_view_total_balance`) 用于显示总余额。
        *   `RecyclerView` (`@+id/recycler_view_balances`) 用于显示货币列表。
        *   `ProgressBar` (`@+id/progress_bar_loading`) 控制显示/隐藏。
        *   `TextView` (`@+id/text_view_error`) 用于显示错误信息，控制显示/隐藏。
    *   `item_balance.xml`:
        *   `ConstraintLayout` 或 `LinearLayout` 作为根。
        *   `ImageView` (`@+id/image_view_currency_icon`) (可选，可暂时用固定图标或颜色)。
        *   `TextView` (`@+id/text_view_currency_name`)。
        *   `TextView` (`@+id/text_view_currency_balance`) (右对齐)。
        *   `TextView` (`@+id/text_view_currency_usd_value`) (右对齐，在余额下方)。
4.  **创建 RecyclerView Adapter (`presentation/dashboard`)**:
    *   `BalanceAdapter.kt`:
        *   继承 `ListAdapter<AggregatedBalance, BalanceAdapter.BalanceViewHolder>(DiffCallback)`。
        *   实现 `DiffUtil.ItemCallback<AggregatedBalance>`。
        *   `BalanceViewHolder` 内部类，持有 `ItemBalanceBinding`。
        *   在 `onCreateViewHolder` 中加载布局和创建 ViewHolder。
        *   在 `onBindViewHolder` 中使用 ViewBinding 绑定 `AggregatedBalance` 数据到视图 (名称、格式化后的余额、格式化后的 USD 价值)。
5.  **创建 Activity/Fragment (`presentation/dashboard`)**:
    *   `DashboardActivity.kt` (或 `DashboardFragment.kt`):
        *   使用 `@AndroidEntryPoint` 注解。
        *   获取 `DashboardViewModel` (`by viewModels()`)。
        *   使用 ViewBinding 获取视图引用。
        *   设置 RecyclerView 的 LayoutManager 和 Adapter。
        *   启动协程 (`lifecycleScope.launch` 或 `viewLifecycleOwner.lifecycleScope.launchWhenStarted`) 收集 `viewModel.uiState` Flow。
        *   在收集回调中，根据 `DashboardUiState` 更新 UI：
            *   更新 `text_view_total_balance` (需要格式化 `BigDecimal` 为 "$ XX.XX USD")。
            *   调用 `adapter.submitList(uiState.balances)`。
            *   根据 `uiState.isLoading` 控制 `progress_bar_loading` 的可见性。
            *   根据 `uiState.error` 控制 `text_view_error` 的可见性和文本内容。

## 阶段六: 数据格式化与细节完善 (约 0.25 天)

1.  **创建格式化工具 (`presentation/util`)**:
    *   `FormattingUtils.kt`: 创建扩展函数或工具类来格式化 `BigDecimal` 为货币字符串 (e.g., `formatAsUsd()`, `formatBalance(symbol: String)`)。考虑小数位数和区域设置。
2.  **应用格式化**: 在 Adapter 的 `onBindViewHolder` 和 Activity/Fragment 更新总余额时使用格式化工具。
3.  **完善聚合逻辑**: 在 `GetAggregatedDashboardDataUseCase` 中仔细处理各种情况（如某种货币有余额但无汇率），确保计算准确，总额计算正确。

## 阶段七: 错误处理与测试 (约 0.5 天)

1.  **测试错误场景**: 手动修改 JSON 文件（例如，使其格式错误、缺少字段、设置汇率为0）来测试应用的错误处理流程。
2.  **编写单元测试**:
    *   测试 `GetAggregatedDashboardDataUseCase` 的聚合和计算逻辑 (使用 Mock Repositories)。
    *   测试 `DashboardViewModel` 的状态映射逻辑 (使用 Mock UseCase 和 `kotlinx-coroutines-test`)。
    *   测试 Mapper 的转换逻辑。
    *   测试 DataSource 的 JSON 解析逻辑。

## 阶段八: 清理与提交 (约 0.25 天)

1.  **代码审查**: 检查代码是否符合 `Android-guidelines.md`。
2.  **移除无用代码**: 清理未使用的导入、变量、资源。
3.  **添加 KDoc**: 为关键的公共类和方法添加文档注释。
4.  **最终测试**: 在模拟器或真机上进行最终的功能测试。
5.  **提交代码**: 提交所有更改到 Git 仓库。

**总计估时**: 约 4.25 天 (此估算较为理想，实际开发可能因调试、环境配置等因素增加时间) 