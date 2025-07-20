package com.example.weyland;


import com.example.weyland.audit.WeylandWatchingYou;
import org.springframework.stereotype.Service;

@Service
public class ReactorControl {

    @WeylandWatchingYou
    public String checkCoreTemperature(String coreId) {
        return "Core " + coreId + " temperature nominal.";
    }
}