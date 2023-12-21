package com.kcube.cubite.com.kcube.cubite

object ExpandableListData {
    val data: HashMap<String, List<String>>
        get() {
            val expandableListDetail = HashMap<String, List<String>>()
            val snackIceCream: MutableList<String> = ArrayList()
            snackIceCream.add("Arun")
            snackIceCream.add("Rithika")
            snackIceCream.add("Haritha")
            val snackPuffs: MutableList<String> = ArrayList()
            snackPuffs.add("Ajith Kumar")
            snackPuffs.add("Arunachalam")
            val snackCupCake: MutableList<String> = ArrayList()
            snackCupCake.add("Bravin")
            snackCupCake.add("Kabil")
            snackCupCake.add("Sathis Kumar")
            snackCupCake.add("Deepa")
            expandableListDetail["Ice Cream"] = snackIceCream
            expandableListDetail["Puffs"] = snackPuffs
            expandableListDetail["Cup Cake"] = snackCupCake
            return expandableListDetail
        }
}