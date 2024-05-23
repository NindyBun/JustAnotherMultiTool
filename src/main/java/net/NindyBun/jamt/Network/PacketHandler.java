package net.NindyBun.jamt.Network;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Network.packets.ExtractModule;
import net.NindyBun.jamt.Network.packets.InsertModule;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
     public static void registerPackets(final RegisterPayloadHandlersEvent event) {
         final PayloadRegistrar registrar = event.registrar(JustAnotherMultiTool.MODID);

         registrar.playToServer(ExtractModule.ExtractModuleData.TYPE, ExtractModule.ExtractModuleData.STREAM_CODEC, ExtractModule.get()::handle);
         registrar.playToServer(InsertModule.InsertModuleData.TYPE, InsertModule.InsertModuleData.STREAM_CODEC, InsertModule.get()::handle);
    }
}
