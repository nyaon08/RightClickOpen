package com.github.nyaon08.rtustudio.rco;

import com.github.nyaon08.rtustudio.rco.commands.MainCommand;
import com.github.nyaon08.rtustudio.rco.listeners.ChestInteractListener;
import kr.rtuserver.framework.bukkit.api.RSPlugin;
import lombok.Getter;

public class RightClickOpen extends RSPlugin {

    @Getter
    private static RightClickOpen instance;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        registerCommand(new MainCommand(this), true);
        registerEvent(new ChestInteractListener(this));
    }

    @Override
    public void disable() {
        instance = null;
    }

}
