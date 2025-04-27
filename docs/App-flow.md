# 应用流程图说明

本文档描述了 Crypto.com Android Wallet Dashboard 应用的核心用户流程和数据流。

## 核心流程

1.  **应用启动**: 用户启动 Wallet Dashboard 应用。

2.  **数据加载与初始化**:
    *   应用后台开始异步加载数据：
        *   从 `json/currencies-json.md` 加载支持的货币列表。
        *   从 `json/live-rates-json.md` 加载美元汇率。
        *   从 `json/wallet-balance-json.md` 加载用户钱包余额。
    *   界面显示加载状态（例如，进度条或占位符）。

3.  **数据处理与计算**:
    *   数据加载完成后，ViewModel 或 UseCase 开始处理数据：
        *   合并货币信息、汇率和余额数据。
        *   对于每种持有的货币，根据其数量和当前汇率计算对应的美元（USD）价值。
        *   公式: `USD Value = Balance Amount * Exchange Rate`

4.  **界面渲染**:
    *   ViewModel 将处理好的数据（包含货币名称、符号、数量、美元价值的列表）通过 StateFlow 或 LiveData 推送给 UI 层。
    *   UI 层（Activity/Fragment）观察数据变化。
    *   隐藏加载状态指示器。
    *   使用 RecyclerView 将聚合后的钱包数据显示在仪表盘界面上，每行展示一种货币及其详情。

5.  **错误处理流程**:
    *   如果在步骤 2 或 3 中发生任何错误（例如，无法读取 JSON 文件、数据解析失败、计算错误）：
        *   捕获异常。
        *   ViewModel 更新 UI 状态以反映错误情况。
        *   UI 层显示用户友好的错误信息（例如，一个 Snackbar 或界面中的错误提示文本），并可能提供重试选项。

## 数据流简述 (基于 MVVM + Clean Architecture)

```mermaid
graph TD
    A[用户操作/应用启动] --> B(Activity/Fragment);
    B -- 请求数据 --> C{DashboardViewModel};
    C -- 调用 UseCase --> D[GetAggregatedDashboardDataUseCase];
    D -- 请求数据 --> E{Repositories (Currency, Rate, Wallet)};
    E -- 获取数据 --> F[Data Sources (Local JSON)];
    F -- 返回原始数据 --> E;
    E -- 返回领域模型数据 --> D;
    D -- 处理/计算/聚合数据 --> C;
    C -- 更新 UI State (Flow/LiveData) --> B;
    B -- 更新 UI (RecyclerView) --> G[用户界面];

    subgraph Presentation Layer
        B
        C
    end

    subgraph Domain Layer
        D
    end

    subgraph Data Layer
        E
        F
    end

    G --- A;
``` 