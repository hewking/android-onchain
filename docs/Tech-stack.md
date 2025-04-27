# 技术选型与技术栈: Crypto.com Android 钱包仪表盘

本文档明确了构建 Crypto.com Android 钱包仪表盘演示应用所采用的技术栈和具体库选型。

## 1. 核心技术

*   **编程语言**: **Kotlin**
    *   理由: Android 开发的官方推荐语言，具有现代、安全、简洁的特性。
*   **核心框架**: **Android SDK**
    *   理由: 构建原生 Android 应用的基础。
*   **最低 SDK 版本**: (待定, 建议 API 24+ (Android 7.0) 以平衡兼容性和现代 API 使用)

## 2. 架构与设计模式

*   **应用架构**: **MVVM (Model-View-ViewModel)** + **Clean Architecture**
    *   理由: 实现关注点分离、提高代码可测试性、可维护性和可扩展性。符合项目要求。
*   **依赖注入 (DI)**: **Hilt**
    *   理由: Google 推荐的 Android 依赖注入库，简化了 Dagger 的使用，特别适合 Android 开发。

## 3. 异步与响应式编程

*   **异步处理**: **Kotlin Coroutines**
    *   理由: Kotlin 中处理异步任务的现代方式，简化了线程管理。
*   **数据流**: **Kotlin Flow**
    *   理由: Coroutines 生态中的响应式流库，用于处理冷/热数据流，非常适合 MVVM 架构中的数据传递和 UI 更新。符合项目要求。

## 4. 用户界面 (UI)

*   **布局**: **Android XML Layouts**
    *   理由: Android 传统的、成熟的 UI 声明方式。虽然 Jetpack Compose 是新趋势，但项目要求（从 README 暗示）可能倾向于传统方式，或为了简化演示。
*   **视图绑定**: **ViewBinding**
    *   理由: 替代 `findViewById` 和 Kotlin Synthetics，提供类型安全的视图访问，减少空指针风险。
*   **核心布局**: **ConstraintLayout**
    *   理由: 强大而灵活的布局系统，用于创建复杂的、响应式的 UI。
*   **UI 组件库**: **Material Components for Android (Material 3)**
    *   理由: 提供符合 Material Design 指南的、预构建的、美观的 UI 组件。
*   **列表显示**: **RecyclerView**
    *   理由: 高效显示大量数据集的标准 Android 组件。

## 5. Android Jetpack 与核心库

*   **生命周期管理**: **AndroidX Lifecycle (ViewModel, LifecycleOwner, LiveData)**
    *   理由: 感知 Android 组件生命周期，安全地管理 UI 相关数据和操作。ViewModel 是 MVVM 的核心。
*   **数据解析**: **kotlinx.serialization**
    *   理由: Kotlin 官方的序列化库，支持 JSON，与 Kotlin 语言特性（如 data class, coroutines）集成良好。

## 6. 测试

*   **单元测试**: **JUnit 4 / JUnit 5**
    *   理由: Java/Kotlin 单元测试的标准框架。
*   **Mocking**: **Mockito-Kotlin / Mockk**
    *   理由: 用于在单元测试中创建模拟对象，隔离测试单元。
*   **UI 测试 (可选)**: **Espresso**
    *   理由: Android 官方 UI 测试框架。
*   **Coroutine 测试**: **kotlinx-coroutines-test**
    *   理由: 提供测试 Kotlin Coroutines 的工具和辅助函数。

## 7. 构建与版本控制

*   **构建系统**: **Gradle** (使用 **Kotlin DSL - `.gradle.kts`**)
    *   理由: Android 项目的标准构建工具。Kotlin DSL 提供更好的类型安全和 IDE 支持。
*   **依赖管理**: **Gradle Version Catalog (`libs.versions.toml`)**
    *   理由: 集中管理项目依赖及其版本，提高可维护性。
*   **版本控制系统**: **Git**
    *   理由: 分布式版本控制系统的行业标准。

## 8. 总结

该技术栈旨在利用现代 Android 开发的最佳实践，构建一个健壮、可维护且满足项目需求的演示应用。重点在于 Kotlin、Coroutines、Flow、MVVM、Clean Architecture 和 Hilt 的结合使用。 