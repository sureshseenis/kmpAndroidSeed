rootProject.name = "kmpAndroidSeed"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
fun <T : Fragment> launchFragmentInHiltContainer(
    fragmentClass: Class<T>,
    fragmentArgs: Bundle? = null,
    themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
    fragmentFactory: FragmentFactory? = null,
    action: Fragment.() -> Unit = {}
) {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(ApplicationProvider.getApplicationContext(), HiltTestActivity::class.java)
    ).putExtra(FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY, themeResId)

    ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        fragmentFactory?.let {
            activity.supportFragmentManager.fragmentFactory = it
        }

        val fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            fragmentClass.classLoader!!,
            fragmentClass.name
        )

        fragment.arguments = fragmentArgs
        activity.supportFragmentManager.beginTransaction()
            .add(android.R.id.content, fragment, null)
            .commitNow()

        fragment.action()
    }
}
pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
