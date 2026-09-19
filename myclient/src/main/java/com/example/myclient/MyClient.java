package com.example.myclient;

import com.example.myclient.modules.ModuleManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.example.myclient.gui.ClickGuiScreen;
import org.lwjgl.glfw.GLFW;

public class MyClient implements ClientModInitializer {
    public static ModuleManager modules;

    @Override
    public void onInitializeClient() {
        modules = new ModuleManager();
        KeyMapping clickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.myclient.clickgui", GLFW.GLFW_KEY_RIGHT_SHIFT, "category.myclient"));
        ClientTickEvents.END_CLIENT_TICK.register(mc -> { if (clickGuiKey.consumeClick()) mc.setScreen(new ClickGuiScreen()); });
        ClientTickEvents.END_CLIENT_TICK.register(modules::tick);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("toggle")
                .then(ClientCommandManager.argument("module", StringArgumentType.word())
                    .suggests((ctx, b) -> {
                        modules.all().forEach(m -> b.suggest(m.getName()));
                        return b.buildFuture();
                    })
                    .executes(ctx -> {
                        String name = StringArgumentType.getString(ctx, "module");
                        var found = modules.byName(name);
                        if (found.isEmpty()) {
                            ctx.getSource().sendFeedback(Component.literal("No module named " + name));
                            return 0;
                        }
                        found.get().toggle();
                        ctx.getSource().sendFeedback(Component.literal(
                            found.get().getName() + (found.get().isEnabled() ? " enabled" : " disabled")));
                        return 1;
                    })));

            dispatcher.register(ClientCommandManager.literal("modules").executes(ctx -> {
                modules.all().forEach(m -> ctx.getSource().sendFeedback(Component.literal(
                    m.getName() + ": " + (m.isEnabled() ? "ON" : "off"))));
                return 1;
            }));
        });
    }
}
