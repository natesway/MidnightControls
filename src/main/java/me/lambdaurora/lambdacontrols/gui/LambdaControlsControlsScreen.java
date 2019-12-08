/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import me.lambdaurora.lambdacontrols.ButtonBinding;
import me.lambdaurora.lambdacontrols.LambdaControls;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents the controls screen.
 */
public class LambdaControlsControlsScreen extends Screen
{
    private final LambdaControlsSettingsScreen parent;
    final         LambdaControls               mod;
    private final Option                       inverts_right_x_axis;
    private final Option                       inverts_right_y_axis;
    private       ControlsListWidget           bindings_list_widget;
    private       ButtonWidget                 reset_button;
    public        ButtonBinding                focused_binding;

    protected LambdaControlsControlsScreen(@NotNull LambdaControlsSettingsScreen parent)
    {
        super(new TranslatableText("lambdacontrols.menu.title.controller_controls"));
        this.parent = parent;
        this.mod = parent.mod;
        this.inverts_right_x_axis = new BooleanOption("lambdacontrols.menu.invert_right_x_axis", game_options -> this.mod.config.does_invert_right_x_axis(),
                (game_options, new_value) -> {
                    synchronized (this.mod.config) {
                        this.mod.config.set_invert_right_x_axis(new_value);
                    }
                });
        this.inverts_right_y_axis = new BooleanOption("lambdacontrols.menu.invert_right_y_axis", game_options -> this.mod.config.does_invert_right_y_axis(),
                (game_options, new_value) -> {
                    synchronized (this.mod.config) {
                        this.mod.config.set_invert_right_y_axis(new_value);
                    }
                });
    }

    @Override
    public void removed()
    {
        this.mod.config.save();
        super.removed();
    }

    @Override
    protected void init()
    {
        this.addButton(this.inverts_right_x_axis.createButton(this.minecraft.options, this.width / 2 - 155, 18, 150));
        this.addButton(this.inverts_right_y_axis.createButton(this.minecraft.options, this.width / 2 - 155 + 160, 18, 150));
        this.bindings_list_widget = new ControlsListWidget(this, this.minecraft);
        this.children.add(this.bindings_list_widget);
        this.reset_button = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll"),
                btn -> ButtonBinding.stream().forEach(binding -> this.mod.config.set_button_binding(binding, binding.get_default_button()))));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("gui.done"),
                btn -> this.minecraft.openScreen(this.parent)));
    }

    // Replacement for Predicate#not as it is Java 11.
    private <T> Predicate<T> not(Predicate<T> target)
    {
        Objects.requireNonNull(target);
        return target.negate();
    }

    @Override
    public void render(int mouse_x, int mouse_y, float delta)
    {
        this.renderBackground();
        this.bindings_list_widget.render(mouse_x, mouse_y, delta);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 16777215);
        this.reset_button.active = ButtonBinding.stream().anyMatch(this.not(ButtonBinding::is_default));
        super.render(mouse_x, mouse_y, delta);
    }
}
