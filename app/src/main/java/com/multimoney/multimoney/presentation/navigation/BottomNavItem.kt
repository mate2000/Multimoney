package com.multimoney.multimoney.presentation.navigation

import com.multimoney.multimoney.R

sealed class BottomNavItem(val icon: Int,val route: String) {
    object Home : BottomNavItem(R.drawable.ic_home_icon,  Screen.HomeBNScreen.route)
    object QuickAction : BottomNavItem(R.drawable.ic_quick_action_icon, Screen.QuickActionBNScreen.route)
    object Products : BottomNavItem(R.drawable.ic_products_icon, Screen.ProductsBNScreen.route)
}
