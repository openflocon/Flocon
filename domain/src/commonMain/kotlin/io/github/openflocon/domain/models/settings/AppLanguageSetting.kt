package io.github.openflocon.domain.models.settings

// The language of the application: System follows the one of the computer, the others force it
enum class AppLanguageSetting(val languageTag: String?) {
    System(null),
    English("en"),
    French("fr"),
    German("de"),
    Spanish("es"),
    PortugueseBrazil("pt-BR"),
    Japanese("ja"),
    Korean("ko"),
    Italian("it"),
    Indonesian("id"),
    Hindi("hi"),
    Turkish("tr"),
    Russian("ru"),
    Ukrainian("uk"),
    Polish("pl"),
    Vietnamese("vi"),
    ChineseSimplified("zh-CN"),
    ChineseTraditionalTaiwan("zh-TW"),
    ChineseTraditionalHongKong("zh-HK"),
    ChineseTraditionalMacau("zh-MO"),
    ;

    companion object {
        val DEFAULT = System
    }
}
