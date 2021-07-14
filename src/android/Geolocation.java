package org.apache.cordova.geolocation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.callback.Callback;

public class Geolocation extends CordovaPlugin {

    String TAG = "GeolocationPlugin";
    Context mContext;
    CallbackContext context;

    String [] permissions = { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };

    @SuppressLint("MissingPermission")
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        LOG.d(TAG, "We are entering execute");
        context = callbackContext;
         if(action.equals("getLocation"))
        {
            if(hasPermisssion())
            {
                mContext = this.cordova.getActivity().getApplicationContext();

                JSONObject position = LocationUtils.getInstance(mContext).getLocation();
                if(position != null) {
                    callbackContext.success(position);
                } else {
                    JSONObject errorMsg = new JSONObject();
                    errorMsg.put("message", "请检查网络或GPS");
                    callbackContext.error(errorMsg);
                }
            }
            else {
                PermissionHelper.requestPermissions(this, 0, permissions);
            }
            return false;
        }
        return false;
    }


    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException
    {
        PluginResult result;
        //This is important if we're using Cordova without using Cordova, but we have the geolocation plugin installed
        if(context != null) {
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_DENIED) {
                    LOG.d(TAG, "Permission Denied!");
                    result = new PluginResult(PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                    context.sendPluginResult(result);
                    return;
                }

            }
            result = new PluginResult(PluginResult.Status.OK);
            context.sendPluginResult(result);
        }
    }

    public boolean hasPermisssion() {
        for(String p : permissions)
        {
            if(!PermissionHelper.hasPermission(this, p))
            {
                return false;
            }
        }
        return true;
    }

    /*
     * We override this so that we can access the permissions variable, which no longer exists in
     * the parent class, since we can't initialize it reliably in the constructor!
     */

    public void requestPermissions(int requestCode)
    {
        PermissionHelper.requestPermissions(this, requestCode, permissions);
    }



}
