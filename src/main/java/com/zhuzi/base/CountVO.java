package com.zhuzi.base;

/**
 * 统计Map结果类
 */
//@Data
public class CountVO<K, V> {

    /*
    * 统计键
    * */
    private K key;

    /*
    * 统计值
    * */
    private V value;


    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
