package com.yc;

import org.ycframework.annotation.*;
@YcConfiguration
@YcComponentScan(basePackages = {"com.yc.dao","com.yc.service"})
@YcPropertySource(value = "db.properties")
public class MyConfig {


}
