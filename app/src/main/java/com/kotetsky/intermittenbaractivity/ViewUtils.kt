package com.kotetsky.intermittenbaractivity

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


inline fun <reified V : View> RecyclerView.ViewHolder.view(@IdRes viewId: Int) =
    ViewByIdDelegate<RecyclerView.ViewHolder, V>(viewId) {
        itemView.findViewById(it)
    }

inline fun <reified V : View> Activity.view(@IdRes viewId: Int) =
    ViewByIdDelegate<Activity, V>(viewId) { findViewById(it) }

inline fun <reified V : View> View.view(@IdRes viewId: Int) =
    ViewByIdDelegate<View, V>(viewId) { findViewById(it) }

inline fun <reified V : View> android.app.Fragment.view(@IdRes viewId: Int) =
    ViewByIdDelegate<android.app.Fragment, V>(viewId) { view!!.findViewById(it) }


class ViewByIdDelegate<H, V : View>(@IdRes private val viewId: Int, finder: (viewId: Int) -> V) :
    ReadOnlyProperty<H, V> {
    private val cached by lazy { finder(viewId) }

    override fun getValue(thisRef: H, property: KProperty<*>) = cached
}