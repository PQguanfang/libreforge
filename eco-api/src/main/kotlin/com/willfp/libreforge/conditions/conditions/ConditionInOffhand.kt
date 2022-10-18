package com.willfp.libreforge.conditions.conditions

import com.willfp.libreforge.conditions.GenericItemCondition

class ConditionInOffhand : GenericItemCondition(
    "in_offhand",
    { it.inventory.itemInOffHand }
)
