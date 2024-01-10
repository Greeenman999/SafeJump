package de.greenman999.safejump

import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.SliderControllerBuilder
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.BooleanController
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.screen.option.KeybindsScreen
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import java.util.*

class SafeJumpConfig {
    companion object {
        var HANDLER: ConfigClassHandler<SafeJumpConfig> = ConfigClassHandler.createBuilder(SafeJumpConfig::class.java)
            .id(Identifier("safejump", "safejumpconfig"))
            .serializer { config: ConfigClassHandler<SafeJumpConfig>? ->
                GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().configDir.resolve("safejump.json5"))
                    .setJson5(true)
                    .build()
            }
            .build()

        @SerialEntry
        var radius: Int = 2

        @SerialEntry
        var enabled: Boolean = true

        @SerialEntry
        var actionBar: Boolean = true

        @SerialEntry
        var displayType: DisplayType = DisplayType.HEIGHT

        fun openConfigScreen(parent: Screen?): Screen {
            return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("safejump.config.title"))
                .category(ConfigCategory.createBuilder()
                    .name(Text.translatable("safejump.config.categories.main.name"))
                    .option(
                        Option.createBuilder<Boolean>()
                            .name(Text.translatable("safejump.config.categories.main.options.enabled.name"))
                            .description(OptionDescription.of(Text.translatable("safejump.config.categories.main.options.enabled.description")))
                            .binding(true, { enabled }, { enabled = it })
                            .controller { opt ->
                                BooleanControllerBuilder.create(opt)
                                    .coloured(true)
                                    .yesNoFormatter()
                            }
                            .build())
                    .option(
                        Option.createBuilder<Boolean>()
                            .name(Text.translatable("safejump.config.categories.main.options.actionbar.name"))
                            .description(OptionDescription.of(Text.translatable("safejump.config.categories.main.options.actionbar.description")))
                            .binding(true, { actionBar }, { actionBar = it })
                            .controller { opt ->
                                BooleanControllerBuilder.create(opt)
                                    .coloured(true)
                                    .yesNoFormatter()
                            }
                            .build())
                    .option(
                        Option.createBuilder<DisplayType>()
                            .name(Text.translatable("safejump.config.categories.main.options.display-type.name"))
                            .description(OptionDescription.of(Text.translatable("safejump.config.categories.main.options.display-type.description")))
                            .binding(DisplayType.HEIGHT, { displayType }, { displayType = it })
                            .controller { opt ->
                                EnumControllerBuilder.create(opt)
                                    .enumClass(DisplayType::class.java)
                            }
                            .build())
                    .option(
                        Option.createBuilder<Int>()
                            .name(Text.translatable("safejump.config.categories.main.options.radius.name"))
                            .description(OptionDescription.of(Text.translatable("safejump.config.categories.main.options.radius.description")))
                            .binding(2, { radius }, { radius = it })
                            .controller { opt ->
                                IntegerSliderControllerBuilder.create(opt)
                                    .range(1, 10)
                                    .step(1)
                            }
                            .build())
                    .option(
                        LabelOption.createBuilder()
                            .line(
                                Text.translatable(
                                    "safejump.config.categories.main.options.keybindings",
                                    SafeJump.keyBinding.boundKeyLocalizedText
                                )
                            )
                            .line(
                                Text.translatable("safejump.config.categories.main.options.keybindings.notice")
                                    .formatted(Formatting.GRAY)
                            )
                            .build()
                    )
                    .option(ButtonOption.createBuilder()
                        .name(Text.translatable("safejump.config.categories.main.options.open-controls.name"))
                        .description(OptionDescription.of(Text.translatable("safejump.config.categories.main.options.open-controls.description")))
                        .text(Text.empty())
                        .action { _: YACLScreen?, _: ButtonOption? ->
                            MinecraftClient.getInstance()
                                .setScreen(KeybindsScreen(parent, MinecraftClient.getInstance().options))
                        }
                        .build())
                    .build())
                .save { HANDLER.save() }
                .build().generateScreen(parent)
        }
    }

    enum class DisplayType : NameableEnum {
        DAMAGE, HEIGHT;

        override fun getDisplayName(): Text {
            return Text.translatable("safejump.config.categories.main.options.display-type.options." + name.lowercase(Locale.getDefault()))
        }
    }

}
