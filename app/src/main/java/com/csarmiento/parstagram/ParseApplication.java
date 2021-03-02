package com.csarmiento.parstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

    ParseObject.registerSubclass(Post.class);

    Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("WuaJsQIlestHQjjm0gl6JWnWUlSJe0k7LAH4sJBf")
        .clientKey("VZwY2pJBhus2NfmUAEUdWVmhO4JF4MCJeYj9Qys2")
        .server("https://parseapi.back4app.com")
        .build()
    );
  }
}
