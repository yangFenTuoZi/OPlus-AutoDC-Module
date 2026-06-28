package yangfentuozi.oplusautodc.daemon

enum class EyeProtectionState(
    val value: String,
    val text: String
) {
    PWM_STATE(
        value = "0",
        text = "经典低频闪"
    ),
    DC_STATE(
        value = "2",
        text = "全亮度低频闪"
    ),
    KEEP_STATE(
        value = "1",
        text = "系统保留状态"
    ),
    UNKNOWN(
        value = "",
        text = "未知"
    );

    override fun toString(): String {
        return text
    }

    companion object {
        fun fromValue(value: String?): EyeProtectionState =
            entries.firstOrNull { it.value == value } ?: UNKNOWN
    }
}
