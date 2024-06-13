package net.NindyBun.jamt.Network;

import net.NindyBun.jamt.JustAnotherMultiTool;
import net.NindyBun.jamt.Network.packets.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler {
     public static void registerPackets(final RegisterPayloadHandlersEvent event) {
         final PayloadRegistrar registrar = event.registrar(JustAnotherMultiTool.MODID);

         registrar.playToServer(ExtractModule.ExtractModuleData.TYPE, ExtractModule.ExtractModuleData.STREAM_CODEC, ExtractModule.get()::handle);
         registrar.playToServer(InsertModule.InsertModuleData.TYPE, InsertModule.InsertModuleData.STREAM_CODEC, InsertModule.get()::handle);
         registrar.playToServer(ServerOpenMultiToolRadialMenu.ServerOpenMultiToolRadialMenuData.TYPE, ServerOpenMultiToolRadialMenu.ServerOpenMultiToolRadialMenuData.STREAM_CODEC, ServerOpenMultiToolRadialMenu.get()::handle);
         registrar.playToServer(SaveModuleSelection.SaveModuleSelectionData.TYPE, SaveModuleSelection.SaveModuleSelectionData.STREAM_CODEC, SaveModuleSelection.get()::handle);

         registrar.playToClient(ClientOpenMultiToolRadialMenu.ClientOpenMultiToolRadialMenuData.TYPE, ClientOpenMultiToolRadialMenu.ClientOpenMultiToolRadialMenuData.STREAM_CODEC, ClientOpenMultiToolRadialMenu.get()::handle);
    }
}
