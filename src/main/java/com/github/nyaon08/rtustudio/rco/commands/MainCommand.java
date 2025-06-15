package com.github.nyaon08.rtustudio.rco.commands;

import com.github.nyaon08.rtustudio.rco.RightClickOpen;
import kr.rtuserver.framework.bukkit.api.command.RSCommand;
import kr.rtuserver.framework.bukkit.api.command.RSCommandData;
import org.bukkit.permissions.PermissionDefault;

public class MainCommand extends RSCommand<RightClickOpen> {

    public MainCommand(RightClickOpen plugin) {
        super(plugin, "rco", PermissionDefault.OP);
    }

    @Override
    protected void reload(RSCommandData data) {
    }

}