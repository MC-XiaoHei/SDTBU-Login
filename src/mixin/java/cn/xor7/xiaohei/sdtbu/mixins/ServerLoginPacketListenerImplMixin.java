package cn.xor7.xiaohei.sdtbu.mixins;

import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLoginPacketListenerImpl.class)
public abstract class ServerLoginPacketListenerImplMixin {
    @Final
    @Shadow
    public Connection connection;

    @Redirect(
            method = "handleHello(Lnet/minecraft/network/protocol/login/ServerboundHelloPacket;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/MinecraftServer;usesAuthentication()Z"
            )
    )
    private boolean redirectUsesAuthentication(MinecraftServer server) {
        return connection.hostname.startsWith("online.");
    }
}