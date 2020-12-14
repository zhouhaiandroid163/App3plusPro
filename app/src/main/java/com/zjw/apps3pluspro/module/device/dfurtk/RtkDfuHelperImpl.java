package com.zjw.apps3pluspro.module.device.dfurtk;

import android.content.Context;
import android.content.res.TypedArray;

import com.realsil.sdk.dfu.DfuConstants;
import com.realsil.sdk.dfu.DfuException;
import com.realsil.sdk.dfu.image.ImageValidateManager;
import com.realsil.sdk.dfu.model.OtaModeInfo;
import com.realsil.sdk.dfu.utils.DfuAdapter;
import com.zjw.apps3pluspro.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @author bingshanguxue
 */
public class RtkDfuHelperImpl {
    public static final ArrayList<OtaModeInfo> SUPPORT_OTA_MODES;

    static {
        SUPPORT_OTA_MODES = new ArrayList<>();
        SUPPORT_OTA_MODES.add(new OtaModeInfo(DfuConstants.OTA_MODE_NORMAL_FUNCTION, R.string.rtk_dfu_work_mode_normal));
        SUPPORT_OTA_MODES.add(new OtaModeInfo(DfuConstants.OTA_MODE_SILENT_EXTEND_FLASH, R.string.rtk_dfu_work_mode_extension));
        SUPPORT_OTA_MODES.add(new OtaModeInfo(DfuConstants.OTA_MODE_SILENT_FUNCTION, R.string.rtk_dfu_work_mode_slient));
        SUPPORT_OTA_MODES.add(new OtaModeInfo(DfuConstants.OTA_MODE_SILENT_FUNCTION, R.string.rtk_dfu_work_mode_silent_no_temp));
    }

    /**
     * parse error message
     * @param context
     * @param type
     * @param code
     * @return
     */
    public static String parseError(Context context, int type, int code) {
        if (type == DfuException.Type.CONNECTION) {
             return parseConnectionErrorCode(context, code);
        } else {
            return RtkDfuHelperImpl.parseErrorCode(context, code);
        }
    }

    /**
     * obtain the referenced arrays
     */
    public static String parseErrorCode(Context context, int code) {
        TypedArray cateDefaultArray = context.getResources().obtainTypedArray(R.array.error_code);
        for (int i = 0; i < cateDefaultArray.length(); i++) {
            String[] errorInfo = context.getResources().getStringArray(cateDefaultArray.getResourceId(i, -1));
            int error = Integer.parseInt(errorInfo[0]);
            String title = errorInfo[1];
            String detail = errorInfo[2];
            if (error == code) {
                return String.format(context.getResources().getString(R.string.rtk_dfu_toast_error_message),
                        String.format(Locale.US, "0x%04X(%d)", error, error), title, detail);
            }
        }

        cateDefaultArray.recycle();

        return String.format(context.getResources().getString(R.string.rtk_dfu_toast_error_message),
                String.valueOf(code), "null", "null");
    }

    public static String parseConnectionErrorCode(Context context, int code) {
        TypedArray cateDefaultArray = context.getResources().obtainTypedArray(R.array.connection_error_code);
        for (int i = 0; i < cateDefaultArray.length(); i++) {
            String[] errorInfo = context.getResources().getStringArray(cateDefaultArray.getResourceId(i, -1));
            int error = Integer.parseInt(errorInfo[0]);
            String title = errorInfo[1];
            String detail = errorInfo[2];
            if (error == code) {
                return String.format(context.getResources().getString(R.string.rtk_dfu_toast_error_message),
                        String.format("0x%04X", error), title, detail);
            }
        }

        cateDefaultArray.recycle();

        return String.format(context.getResources().getString(R.string.rtk_dfu_toast_error_message),
                String.valueOf(code), "null", "null");
    }



    public static String parseImageValidateError(Context context, int code) {
        if (context == null) {
            throw new IllegalArgumentException("context can not be null");
        }

        switch (code) {
            case ImageValidateManager.ERR_NA:
                return context.getString(R.string.rtk_dfu_fc_undefined);
            case ImageValidateManager.ERR_PACK_LOSS:
                return context.getString(R.string.rtk_dfu_fc_pack_loss);
            case ImageValidateManager.ERR_PACK_LOSS_EMPTY:
                return context.getString(R.string.rtk_dfu_fc_pack_loss_empty);
            case ImageValidateManager.ERR_PACK_DUPLICATE_BANK:
                return context.getString(R.string.rtk_dfu_fc_pack_duplicate_bank);
            case ImageValidateManager.ERR_PACK_LOSS_OTA_HEADER:
                return context.getString(R.string.rtk_dfu_fc_pack_loss_ota_header);
            case ImageValidateManager.ERR_PACK_LOSS_ROM_PATCH:
                return context.getString(R.string.rtk_dfu_fc_pack_loss_rom_patch);
            case ImageValidateManager.ERR_PACK_LOSS_APP_IMAGE:
                return context.getString(R.string.rtk_dfu_fc_pack_loss_app_image);
            case ImageValidateManager.ERR_PACK_LOW_VERSION:
                return context.getString(R.string.rtk_dfu_fc_pack_loss_low_version);
            case ImageValidateManager.ERR_PACK_NT:
                return context.getString(R.string.rtk_dfu_fc_pack_nt);
            case ImageValidateManager.ERR_PACK_NT_OTA_HEADER:
                return context.getString(R.string.rtk_dfu_fc_pack_nt_ota_header);
            case ImageValidateManager.ERR_PACK_NT_BANK_SWITCH:
                return context.getString(R.string.rtk_dfu_fc_pack_nt_bank_switch);
            case ImageValidateManager.ERR_SINGLE_NT:
                return context.getString(R.string.rtk_dfu_fc_single_nt);
            default:
                return context.getString(R.string.rtk_dfu_fc_undefined);
        }
    }

    public static int getWorkModeNameResId(int type) {
        switch (type) {
            case DfuConstants.OTA_MODE_NORMAL_FUNCTION:
                return R.string.rtk_dfu_work_mode_normal;
            case DfuConstants.OTA_MODE_SILENT_FUNCTION:
                return R.string.rtk_dfu_work_mode_slient;
            case DfuConstants.OTA_MODE_SILENT_EXTEND_FLASH:
                return R.string.rtk_dfu_work_mode_extension;
            case DfuConstants.OTA_MODE_SILENT_NO_TEMP:
                return R.string.rtk_dfu_work_mode_silent_no_temp;
            default:
                return R.string.rtk_dfu_work_mode_unknown;
        }
    }

    /**
     * @param state state
     * @return
     * @deprecated please use {@link #getProgressStateResId(int)} instead
     */
    public static int getStateResId(int state) {
        return getProgressStateResId(state);
    }

    /**
     * @param state state
     * @return
     */
    public static int getAdapterStateResId(int state) {
        switch (state) {
            case DfuAdapter.STATE_INIT:
                return R.string.rtk_ota_state_oritin;
            case DfuAdapter.STATE_INIT_BINDING_SERVICE:
                return R.string.rtk_ota_state_bind_service;
            case DfuAdapter.STATE_INIT_OK:
                return R.string.rtk_ota_state_init_ok;
            case DfuAdapter.STATE_PREPARING:
                return R.string.rtk_ota_state_preparing;
            case DfuAdapter.STATE_HID_CONNECTING:
            case DfuAdapter.STATE_HID_PENDING_REMOVE_BOND:
            case DfuAdapter.STATE_HID_PENDING_CREATE_BOND:
            case DfuAdapter.STATE_CONNECTING:
                return R.string.rtk_ota_state_connecting;
            case DfuAdapter.STATE_PROCESS_PAIRING_REQUEST:
                return R.string.rtk_ota_state_process_pair_request;
            case DfuAdapter.STATE_PENDDING_DISCOVERY_SERVICE:
                return R.string.rtk_ota_state_pending_discover_service;
            case DfuAdapter.STATE_DISCOVERY_SERVICE:
                return R.string.rtk_ota_state_discover_service;
            case DfuAdapter.STATE_READ_DEVICE_INFO:
                return R.string.rtk_ota_state_read_device_info;
            case DfuAdapter.STATE_READ_IMAGE_INFO:
                return R.string.rtk_ota_state_read_image_info;
            case DfuAdapter.STATE_READ_BATTERY_INFO:
                return R.string.rtk_ota_state_read_battery_info;
            case DfuAdapter.STATE_PREPARED:
                return R.string.rtk_ota_state_prepared;
            case DfuAdapter.STATE_OTA_PROCESSING:
                return R.string.rtk_ota_state_ota_processing;
            case DfuAdapter.STATE_DISCONNECTING:
                return R.string.rtk_dfu_connection_state_disconnecting;
            case DfuAdapter.STATE_DISCONNECTED:
                return R.string.rtk_dfu_connection_state_disconnected;
            case DfuAdapter.STATE_CONNECT_FAILED:
                return R.string.rtk_dfu_connection_state_disconnected;
            case DfuAdapter.STATE_ABORTED:
                return R.string.rtk_dfu_state_abort_processing;
            default:
                return R.string.rtk_dfu_state_known;
        }
    }

    /**
     * @param state state
     * @return
     */
    public static int getProgressStateResId(int state) {
        switch (state) {
            case DfuConstants.PROGRESS_ORIGIN:
                return R.string.rtk_dfu_progress_state_origin;
            case DfuConstants.PROGRESS_INITIALIZE:
                return R.string.rtk_dfu_state_initialize;
            case DfuConstants.PROGRESS_STARTED:
                return R.string.rtk_dfu_state_start;
            case DfuConstants.PROGRESS_REMOTE_ENTER_OTA:
                return R.string.rtk_dfu_state_remote_enter_ota;
            case DfuConstants.PROGRESS_SCAN_REMOTE:
            case DfuConstants.PROGRESS_SCAN_OTA_REMOTE:
                return R.string.rtk_dfu_state_find_ota_remote;
            case DfuConstants.PROGRESS_CONNECT_REMOTE:
            case DfuConstants.PROGRESS_CONNECT_OTA_REMOTE:
                return R.string.rtk_dfu_state_connect_ota_remote;
            case DfuConstants.PROGRESS_PREPARE_OTA_ENVIRONMENT:
                return R.string.rtk_dfu_state_prepare_dfu_processing;
            case DfuConstants.PROGRESS_START_DFU_PROCESS:
                return R.string.rtk_dfu_state_start_ota_processing;
            case DfuConstants.PROGRESS_HAND_OVER_PROCESSING:
                return R.string.rtk_dfu_state_hand_over_processing;
            case DfuConstants.PROGRESS_PENDING_ACTIVE_IMAGE:
                return R.string.rtk_dfu_state_pending_active_image;
            case DfuConstants.PROGRESS_ACTIVE_IMAGE_AND_RESET:
                return R.string.rtk_dfu_state_start_active_image;
            case DfuConstants.PROGRESS_IMAGE_ACTIVE_SUCCESS:
                return R.string.rtk_dfu_state_image_active_success;
            case DfuConstants.PROGRESS_ABORT_PROCESSING:
                return R.string.rtk_dfu_state_abort_processing;
            case DfuConstants.PROGRESS_PROCESSING_ERROR:
                return R.string.rtk_dfu_state_error_processing;
            case DfuConstants.PROGRESS_ABORTED:
                return R.string.rtk_dfu_state_aborted;
            default:
                return R.string.rtk_dfu_state_known;
        }
    }



}
