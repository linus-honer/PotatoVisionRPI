package org.potato.core.network.networktables;

import org.potato.core.network.IPAddressMode;

public class NTConfig {
    public String serverAddress = "0";
    public IPAddressMode addressMode = IPAddressMode.DHCP;
    public String staticIP = "";
    public String hostName = "potatovision";
    public boolean hostServer;
}
