package com.app.ia.utils

import java.io.IOException
import java.util.*

class ServiceUtility {

    @Throws(IOException::class)
    fun readProperties(pFilePath: String?): Map<*, *> {
        var vPropertyMap: MutableMap<*, *> = LinkedHashMap<Any?, Any?>()
        var vTckey: Set<*>?
        var vTcPropItr: Iterator<Any>?
        var vTCProperties: Properties? = null
        try {
            vTCProperties = Properties()
            vTCProperties.load(ServiceUtility::class.java.getResourceAsStream(pFilePath))
            vTckey = vTCProperties.keys
            vTcPropItr = vTckey.iterator()
            vPropertyMap = LinkedHashMap<Any?, Any?>()
            while (vTcPropItr.hasNext()) {
                val vKey = vTcPropItr.next()
                vPropertyMap[vKey] = vTCProperties[vKey]
            }
        } finally {
            vTcPropItr = null
            vTckey = null
            vTCProperties = null
        }
        return vPropertyMap
    }

    companion object {
        fun chkNull(pData: String?): String {
            return pData ?: ""
        }

        @Throws(Exception::class)
        fun tokenizeToHashMap(msg: String?, delimPairValue: String, delimKeyPair: String?): Map<*, *>? {
            val keyPair: MutableMap<String?, String?> = HashMap<String?, String?>()
            var respList: ArrayList<String>?
            var part: String?
            val strTkn = StringTokenizer(msg, delimPairValue, true)
            while (strTkn.hasMoreTokens()) {
                part = strTkn.nextElement() as String
                if (part == delimPairValue) {
                    part = null
                } else {
                    respList = tokenizeToArrayList(part, delimKeyPair)
                    if (respList!!.size == 2) keyPair[respList[0]] = respList[1] else if (respList.size == 1) keyPair[respList[0]] = null
                }
                if (part == null) continue
                if (strTkn.hasMoreTokens()) strTkn.nextElement()
            }
            return if (keyPair.isNotEmpty()) keyPair else null
        }

        @Throws(Exception::class)
        fun tokenizeToArrayList(msg: String?, delim: String?): ArrayList<String>? {
            val respList: ArrayList<String> = ArrayList<String>()
            val varName: String?
            var varVal: String? = null
            val index = msg!!.indexOf(delim!!)
            varName = msg.substring(0, index)
            if (index + 1 != msg.length) varVal = msg.substring(index + 1, msg.length)
            respList.add(varName)
            respList.add(varVal!!)
            return if (respList.size > 0) respList else null
        }

        fun addToPostParams(paramKey: String, paramValue: String?): String {
            return if (paramValue != null) paramKey + AppConstants.PARAMETER_EQUALS + paramValue + AppConstants.PARAMETER_SEP else ""
        }

        fun randInt(min: Int, max: Int): Int {
            // Usually this should be a field rather than a method variable so
            // that it is not re-seeded every call.
            val rand = Random()

            // nextInt is normally exclusive of the top value,
            // so add 1 to make it inclusive
            return rand.nextInt(max - min + 1) + min
        }
    }
}