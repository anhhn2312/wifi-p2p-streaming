package com.andyha.coreui.base.fragment

import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.andyha.coreui.base.ui.widget.BaseToolbar


class ToolbarConfiguration(builder: Builder) {
    var isDefaultToolbarEnabled: Boolean = true

    @StringRes
    var title: Int? = null
    var titleStr: String? = null
    var hasBackButton: Boolean = true
    var hasRefreshLayout: Boolean = true
    var titleStyle: TitleStyle = TitleStyle.REGULAR

    @DrawableRes
    var menuIcon: Int? = null

    @DrawableRes
    var menuVisibility: Int = View.GONE

    @StringRes
    var menuText: Int? = null
    var customToolbar: BaseToolbar? = null

    init {
        this.isDefaultToolbarEnabled = builder.isDefaultToolbarEnabled
        this.title = builder.title
        this.titleStr = builder.titleStr
        this.titleStyle = builder.titleStyle
        this.hasBackButton = builder.hasBackButton
        this.menuIcon = builder.menuIcon
        this.menuVisibility = builder.menuVisibility
        this.menuText = builder.menuText
        this.hasRefreshLayout = builder.hasRefreshLayout
        this.customToolbar = builder.customToolbar
    }

    class Builder {
        internal var isDefaultToolbarEnabled: Boolean = true
        internal var title: Int? = null
        internal var titleStr: String? = null
        internal var titleStyle: TitleStyle = TitleStyle.REGULAR
        internal var hasBackButton: Boolean = true
        internal var menuIcon: Int? = null
        internal var menuVisibility: Int = View.GONE
        internal var menuText: Int? = null
        internal var hasRefreshLayout: Boolean = true
        internal var hasBackground: Boolean = true
        internal var customToolbar: BaseToolbar? = null

        /**
         * On back pressed
         *
         * @return: True if override function
         * false: default back
         */
        open fun onBackPressed(): Boolean {
            return false
        }

        fun setDefaultToolbarEnabled(isEnabled: Boolean): Builder {
            this.isDefaultToolbarEnabled = isEnabled
            return this
        }

        fun setTitle(title: Int): Builder {
            this.title = title
            return this
        }

        fun setTitle(titleStr: String): Builder {
            this.titleStr = titleStr
            return this
        }

        fun setTitleStyle(style: TitleStyle): Builder {
            this.titleStyle = style
            return this
        }

        fun setHasBackButton(hasBackButton: Boolean): Builder {
            this.hasBackButton = hasBackButton
            return this
        }

        fun setMenuIcon(@DrawableRes menuIcon: Int): Builder {
            this.menuIcon = menuIcon
            return this
        }

        fun setMenuIconVisibility(menuVisibility: Int): Builder {
            this.menuVisibility = menuVisibility
            return this
        }

        fun setMenuText(@StringRes menuText: Int): Builder {
            this.menuText = menuText
            return this
        }

        fun setHasRefreshLayout(hasRefreshLayout: Boolean): Builder {
            this.hasRefreshLayout = hasRefreshLayout
            return this
        }

        fun setHasBackground(isHasBackGround: Boolean): Builder {
            this.hasBackground = isHasBackGround
            return this
        }

        fun setCustomToolbar(customToolbar: BaseToolbar?): Builder {
            this.customToolbar = customToolbar
            return this
        }

        fun build(): ToolbarConfiguration {
            return ToolbarConfiguration(this)
        }
    }
}

enum class TitleStyle {
    REGULAR, BIG
}