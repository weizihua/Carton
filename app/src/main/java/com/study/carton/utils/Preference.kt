package com.study.carton.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import java.io.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty



class Preference<T>(val name: String, private val default: T) : ReadWriteProperty<Any?, T> {

    companion object {
        const val PREVIEW_HORIZONTAL_MODEL = "preview_horizontal_model"
        //是否是夜间模式
        const val NIGHT_BRIGHTNESS_MODEL = "NIGHT_BRIGHTNESS"
        lateinit var preferences: SharedPreferences
        fun setContext(context: Context){
            preferences = context.getSharedPreferences(context.packageName,Context.MODE_PRIVATE)
        }
        fun clear()= preferences.edit().clear().apply()
    }
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getValue(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putValue(name, value)
    }

    @SuppressLint("CommitPrefEdits")
    private fun <T> putValue(name: String, value: T) = with(preferences.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name, serialize(value))
        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(name: String, default: T): T = with(preferences) {
        val res: Any? = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> getString(name, serialize(default))?.let { deSerialization<Any?>(it) }
        }
        return res as T
    }

    /**
     * 删除全部数据
     */
    fun clearPreference() {
        preferences.edit().clear().apply()
    }

    /**
     * 根据key删除存储数据
     */
    fun clearPreference(key: String) {
        preferences.edit().remove(key).apply()
    }

    /**
     * 序列化对象
     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
                byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象
     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
                redStr.toByteArray(charset("ISO-8859-1")))
        val objectInputStream = ObjectInputStream(
                byteArrayInputStream)
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }


    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    fun contains(key: String): Boolean {
        return preferences.contains(key)
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    fun getAll(): Map<String, *> {
        return preferences.all
    }
}