package com.simplicity.maged.mccobjectdetection.components.contextManager;

import java.util.UUID;

public class EnvironmentContext {
    public UUID providerUUID;
    public double upBW3; // Cloud<-->Mobile_System
    public double downBW3;
    public double upBW4; // Cloud<-->Data_Provider
    public double downBW4;
    public double upBW5; // Cloud<-->User_App
    public double downBW5;
    public long computeSpeed;
    public double memoryAvail;
    public double energyAvail;
}
