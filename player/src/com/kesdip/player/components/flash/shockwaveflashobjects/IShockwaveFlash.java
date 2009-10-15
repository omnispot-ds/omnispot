package com.kesdip.player.components.flash.shockwaveflashobjects;

import com.jniwrapper.*;
import com.jniwrapper.win32.*;
import com.jniwrapper.win32.automation.*;
import com.jniwrapper.win32.automation.impl.*;
import com.jniwrapper.win32.automation.types.*;
import com.jniwrapper.win32.com.*;
import com.jniwrapper.win32.com.impl.*;
import com.jniwrapper.win32.com.types.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.impl.*;

/**
 * Represents Java interface for COM interface IShockwaveFlash.
 */
public interface IShockwaveFlash extends IDispatch
{
    public static final String INTERFACE_IDENTIFIER = "{D27CDB6C-AE6D-11CF-96B8-444553540000}";

    Int32 getReadyState()
        throws ComException;

    Int32 getTotalFrames()
        throws ComException;

    VariantBool getPlaying()
        throws ComException;

    void setPlaying(
        VariantBool /*[in]*/ pVal)
        throws ComException;

    Int getQuality()
        throws ComException;

    void setQuality(
        Int /*[in]*/ pVal)
        throws ComException;

    Int getScaleMode()
        throws ComException;

    void setScaleMode(
        Int /*[in]*/ pVal)
        throws ComException;

    Int getAlignMode()
        throws ComException;

    void setAlignMode(
        Int /*[in]*/ pVal)
        throws ComException;

    Int32 getBackgroundColor()
        throws ComException;

    void setBackgroundColor(
        Int32 /*[in]*/ pVal)
        throws ComException;

    VariantBool getLoop()
        throws ComException;

    void setLoop(
        VariantBool /*[in]*/ pVal)
        throws ComException;

    BStr getMovie()
        throws ComException;

    void setMovie(
        BStr /*[in]*/ pVal)
        throws ComException;

    Int32 getFrameNum()
        throws ComException;

    void setFrameNum(
        Int32 /*[in]*/ pVal)
        throws ComException;

    void setZoomRect(
        Int32 /*[in]*/ left,
        Int32 /*[in]*/ top,
        Int32 /*[in]*/ right,
        Int32 /*[in]*/ bottom)
        throws ComException;

    void zoom(
        Int /*[in]*/ factor)
        throws ComException;

    void pan(
        Int32 /*[in]*/ x,
        Int32 /*[in]*/ y,
        Int /*[in]*/ mode)
        throws ComException;

    void play()
        throws ComException;

    void stop()
        throws ComException;

    void back()
        throws ComException;

    void forward()
        throws ComException;

    void rewind()
        throws ComException;

    void stopPlay()
        throws ComException;

    void gotoFrame(
        Int32 /*[in]*/ FrameNum)
        throws ComException;

    Int32 currentFrame()
        throws ComException;

    VariantBool isPlaying()
        throws ComException;

    Int32 percentLoaded()
        throws ComException;

    VariantBool frameLoaded(
        Int32 /*[in]*/ FrameNum)
        throws ComException;

    Int32 flashVersion()
        throws ComException;

    BStr getWMode()
        throws ComException;

    void setWMode(
        BStr /*[in]*/ pVal)
        throws ComException;

    BStr getSAlign()
        throws ComException;

    void setSAlign(
        BStr /*[in]*/ pVal)
        throws ComException;

    VariantBool getMenu()
        throws ComException;

    void setMenu(
        VariantBool /*[in]*/ pVal)
        throws ComException;

    BStr getBase()
        throws ComException;

    void setBase(
        BStr /*[in]*/ pVal)
        throws ComException;

    BStr getScale()
        throws ComException;

    void setScale(
        BStr /*[in]*/ pVal)
        throws ComException;

    VariantBool getDeviceFont()
        throws ComException;

    void setDeviceFont(
        VariantBool /*[in]*/ pVal)
        throws ComException;

    VariantBool getEmbedMovie()
        throws ComException;

    void setEmbedMovie(
        VariantBool /*[in]*/ pVal)
        throws ComException;

    BStr getBGColor()
        throws ComException;

    void setBGColor(
        BStr /*[in]*/ pVal)
        throws ComException;

    BStr getQuality2()
        throws ComException;

    void setQuality2(
        BStr /*[in]*/ pVal)
        throws ComException;

    void loadMovie(
        Int /*[in]*/ layer,
        BStr /*[in]*/ url)
        throws ComException;

    void TGotoFrame(
        BStr /*[in]*/ target,
        Int32 /*[in]*/ FrameNum)
        throws ComException;

    void TGotoLabel(
        BStr /*[in]*/ target,
        BStr /*[in]*/ label)
        throws ComException;

    Int32 TCurrentFrame(
        BStr /*[in]*/ target)
        throws ComException;

    BStr TCurrentLabel(
        BStr /*[in]*/ target)
        throws ComException;

    void TPlay(
        BStr /*[in]*/ target)
        throws ComException;

    void TStopPlay(
        BStr /*[in]*/ target)
        throws ComException;

    void setVariable(
        BStr /*[in]*/ name,
        BStr /*[in]*/ value)
        throws ComException;

    BStr getVariable(
        BStr /*[in]*/ name)
        throws ComException;

    void TSetProperty(
        BStr /*[in]*/ target,
        Int /*[in]*/ property,
        BStr /*[in]*/ value)
        throws ComException;

    BStr TGetProperty(
        BStr /*[in]*/ target,
        Int /*[in]*/ property)
        throws ComException;

    void TCallFrame(
        BStr /*[in]*/ target,
        Int /*[in]*/ FrameNum)
        throws ComException;

    void TCallLabel(
        BStr /*[in]*/ target,
        BStr /*[in]*/ label)
        throws ComException;

    void TSetPropertyNum(
        BStr /*[in]*/ target,
        Int /*[in]*/ property,
        DoubleFloat /*[in]*/ value)
        throws ComException;

    DoubleFloat TGetPropertyNum(
        BStr /*[in]*/ target,
        Int /*[in]*/ property)
        throws ComException;

    DoubleFloat TGetPropertyAsNumber(
        BStr /*[in]*/ target,
        Int /*[in]*/ property)
        throws ComException;

    BStr getSWRemote()
        throws ComException;

    void setSWRemote(
        BStr /*[in]*/ pVal)
        throws ComException;

    BStr getFlashVars()
        throws ComException;

    void setFlashVars(
        BStr /*[in]*/ pVal)
        throws ComException;

    BStr getAllowScriptAccess()
        throws ComException;

    void setAllowScriptAccess(
        BStr /*[in]*/ pVal)
        throws ComException;

    BStr getMovieData()
        throws ComException;

    void setMovieData(
        BStr /*[in]*/ pVal)
        throws ComException;

    IUnknown getInlineData()
        throws ComException;

    void setInlineData(
        IUnknown /*[in]*/ ppIUnknown)
        throws ComException;

    VariantBool getSeamlessTabbing()
        throws ComException;

    void setSeamlessTabbing(
        VariantBool /*[in]*/ pVal)
        throws ComException;

    void enforceLocalSecurity()
        throws ComException;

    VariantBool getProfile()
        throws ComException;

    void setProfile(
        VariantBool /*[in]*/ pVal)
        throws ComException;

    BStr getProfileAddress()
        throws ComException;

    void setProfileAddress(
        BStr /*[in]*/ pVal)
        throws ComException;

    Int32 getProfilePort()
        throws ComException;

    void setProfilePort(
        Int32 /*[in]*/ pVal)
        throws ComException;

    BStr callFunction(
        BStr /*[in]*/ request)
        throws ComException;

    void setReturnValue(
        BStr /*[in]*/ returnValue)
        throws ComException;

    void disableLocalSecurity()
        throws ComException;

    BStr getAllowNetworking()
        throws ComException;

    void setAllowNetworking(
        BStr /*[in]*/ pVal)
        throws ComException;

    BStr getAllowFullScreen()
        throws ComException;

    void setAllowFullScreen(
        BStr /*[in]*/ pVal)
        throws ComException;
}
