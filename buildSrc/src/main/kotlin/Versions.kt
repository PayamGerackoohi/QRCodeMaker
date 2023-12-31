import org.gradle.api.JavaVersion

object Versions {
    val app = App()
    val java = JavaVersion.VERSION_17
    val kotlin = "1.8.10"
    val dependencies = Dependencies()

    class App {
        val code = 1
        val name = "1.0.0"

        operator fun invoke(action: App.() -> Unit) = action()
    }

    class Dependencies {
        val mavericks = "3.0.7"
        val hilt = "2.44"
        val room = "2.6.1"
        val android = Android()
        val test = Test()

        class Android {
            val core = "1.9.0"
            val lifecycleRuntime = "2.6.2"
            val compose = Compose()

            class Compose {
                val activity = "1.8.0"
                val bom = "2023.10.01"
                val icons = "1.5.3"
                val nav = "2.7.5"
            }
        }

        class Test {
            val assertj = "3.11.1"
            val mockk = "1.13.8"
            val jUnit = "5.10.0"
            val espresso = "3.5.1"
            val jacoco = "0.8.11"
            val android = Android()
            val kotlin = Kotlin()

            class Android {
                val runner = "1.5.2"
            }

            class Kotlin {
                val coroutine = "1.6.4"
            }
        }

        operator fun invoke(action: Dependencies.() -> Unit) = action()
    }
}
