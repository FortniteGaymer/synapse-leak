package com.ragemachine.moon.api.event.events;

import net.minecraft.network.Packet;


public class EventPacketRecieve
  extends EventCancellable
{
  private Packet packet;
  
  public EventPacketRecieve(Packet packet)
  {
    this.packet = packet;
  }
  
  public Packet getPacket()
  {
    return this.packet;
  }
}
