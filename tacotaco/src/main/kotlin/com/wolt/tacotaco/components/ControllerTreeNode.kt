package com.wolt.tacotaco.components

interface ControllerTreeNode {

    val backstacks: Map<Int, List<ControllerTreeNode>>
    val parent: ControllerTreeNode?
    val tag: String

}
