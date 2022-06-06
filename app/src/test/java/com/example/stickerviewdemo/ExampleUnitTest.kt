package com.example.stickerviewdemo

import org.junit.Test

import org.junit.Assert.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)

    }
}

class Solution {
    fun findDisappearedNumbers(nums: IntArray): List<Int> {
        nums.forEach {
            if(nums[it-1] > 0 ){
                nums[it-1] *= -1
            }
        }
        val list = ArrayList<Int>()
        for(i in nums.indices){
            if(nums[i] > 0)
                list.add(i)
        }
        return list
    }
}
