package dev.httpmarco.polocloud.bridges.waterdog;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import dev.httpmarco.polocloud.api.Polocloud;
import dev.httpmarco.polocloud.api.platform.PlatformType;
import dev.httpmarco.polocloud.api.services.ClusterService;
import dev.httpmarco.polocloud.api.services.events.ClusterServiceOnlineEvent;
import dev.httpmarco.polocloud.api.services.events.ClusterServiceStopEvent;
import dev.waterdog.waterdogpe.ProxyServer;
import dev.waterdog.waterdogpe.network.serverinfo.BedrockServerInfo;
import dev.waterdog.waterdogpe.plugin.Plugin;

import java.net.InetSocketAddress;

public class WaterdogBootstrap extends Plugin {

    @Override
    public void onEnable() {

        // drop all default instances
        for (var server : ProxyServer.getInstance().getServers()) {
            ProxyServer.getInstance().removeServerInfo(server.getServerName());
        }

        var provider = Polocloud.instance().eventProvider();
        provider.subscribe(ClusterServiceOnlineEvent.class, it -> registerService(it.service()));

        provider.subscribe(ClusterServiceStopEvent.class,
                it -> {
                    if (ProxyServer.getInstance().getServerInfo(it.service().name()) != null) {
                        ProxyServer.getInstance().removeServerInfo(it.service().name());
                    }
                }
        );

        for (var service : Polocloud.instance().serviceProvider().findAll()) {
            if (service.group().platform().type() != PlatformType.SERVER) {
                continue;
            }
            registerService(service);
        }
    }

    private void registerService(ClusterService service) {
        ProxyServer.getInstance().registerServerInfo(
                new BedrockServerInfo(service.name(),
                        new InetSocketAddress(service.hostname(), service.port()), null)
        );
    }

    public void unregisterService(RegisteredServer registeredServer) {
        ProxyServer.getInstance().removeServerInfo(registeredServer.getServerInfo().getName());
    }
}
