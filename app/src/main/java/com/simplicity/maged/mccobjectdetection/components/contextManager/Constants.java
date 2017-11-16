package com.simplicity.maged.mccobjectdetection.components.contextManager;

public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION =
                "com.simplicity.maged.mccobjectdetection.components.contextManager.action.main";
        public static String STARTFOREGROUND_ACTION =
                "com.simplicity.maged.mccobjectdetection.components.contextManager.ContextEngineService.action.startforeground";
        public static String STOPFOREGROUND_ACTION =
                "com.simplicity.maged.mccobjectdetection.components.contextManager.ContextEngineService.action.stopforeground";
        public static String STARTPROCESSING_ACTION =
                "com.simplicity.maged.mccobjectdetection.components.contextManager.ContextEngineService.action.startprocessing";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
