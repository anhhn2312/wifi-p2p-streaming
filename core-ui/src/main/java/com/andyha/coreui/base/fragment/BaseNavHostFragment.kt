package com.andyha.coreui.base.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.andyha.coreui.R

// Those are navigation-ui (androidx.navigation.ui) defaults
// used in NavigationUI for NavigationView and BottomNavigationView.
// Set yours here
private val defaultNavOptions = navOptions {
    anim {
        enter = R.anim.nav_default_enter_anim
        exit = R.anim.nav_default_exit_anim
        popEnter = R.anim.nav_default_pop_enter_anim
        popExit = R.anim.nav_default_pop_exit_anim
    }
}

private val defaultPopupToNavOptions = navOptions {
    anim {
        enter = R.anim.nav_default_pop_enter_anim
        exit = R.anim.nav_default_pop_exit_anim
        popEnter = R.anim.nav_default_pop_enter_anim
        popExit = R.anim.nav_default_pop_exit_anim
    }
}

private val emptyNavOptions = navOptions {}

class BaseNavHostFragment : NavHostFragment() {

    override fun onCreateNavController(navController: NavController) {
        super.onCreateNavController(navController)
        navController.navigatorProvider.addNavigator(
            // this replaces FragmentNavigator
            FragmentNavigatorWithDefaultAnimations(requireContext(), childFragmentManager, id)
        )
    }
}

/**
 * Needs to replace FragmentNavigator and replacing is done with name in annotation.
 * Navigation method will use defaults for fragments transitions animations.
 */
@Navigator.Name("fragment")
class FragmentNavigatorWithDefaultAnimations(
    context: Context,
    manager: FragmentManager,
    containerId: Int
) : FragmentNavigator(context, manager, containerId) {

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        // this will try to fill in empty animations with defaults when no shared element transitions are set
        // https://developer.android.com/guide/navigation/navigation-animate-transitions#shared-element
        val shouldUseTransitionsInstead = navigatorExtras != null
        val navOptions = if (shouldUseTransitionsInstead) navOptions
        else navOptions.fillEmptyAnimationsWithDefaults()
        return super.navigate(destination, args, navOptions, navigatorExtras)
    }

    private fun NavOptions?.fillEmptyAnimationsWithDefaults(): NavOptions =
        this?.copyNavOptionsWithDefaultAnimations() ?: defaultNavOptions

    private fun NavOptions.copyNavOptionsWithDefaultAnimations(): NavOptions =
        let { originalNavOptions ->
            navOptions {
                launchSingleTop = originalNavOptions.shouldLaunchSingleTop()
                popUpTo(originalNavOptions.popUpTo) {
                    inclusive = originalNavOptions.isPopUpToInclusive
                }

                val shouldUseOriginalAnimation = shouldUseOriginalAnimation(originalNavOptions)
                if (shouldUseOriginalAnimation) {
                    anim {
                        enter = originalNavOptions.enterAnim
                        exit = originalNavOptions.exitAnim
                        popEnter = originalNavOptions.popEnterAnim
                        popExit = originalNavOptions.popExitAnim
                    }
                    return@navOptions
                }
                if (popUpTo != -1) {
                    anim {
                        enter = defaultPopupToNavOptions.enterAnim
                        exit = defaultPopupToNavOptions.exitAnim
                        popEnter = defaultPopupToNavOptions.popEnterAnim
                        popExit = defaultPopupToNavOptions.popExitAnim
                    }
                } else {
                    anim {
                        enter = defaultNavOptions.enterAnim
                        exit = defaultNavOptions.exitAnim
                        popEnter = defaultNavOptions.popEnterAnim
                        popExit = defaultNavOptions.popExitAnim
                    }
                }
            }
        }

    private fun shouldUseOriginalAnimation(navOptions: NavOptions): Boolean {
        return navOptions.enterAnim != emptyNavOptions.enterAnim ||
                navOptions.exitAnim != emptyNavOptions.exitAnim ||
                navOptions.popEnterAnim != emptyNavOptions.popEnterAnim ||
                navOptions.popExitAnim != emptyNavOptions.popExitAnim
    }
}