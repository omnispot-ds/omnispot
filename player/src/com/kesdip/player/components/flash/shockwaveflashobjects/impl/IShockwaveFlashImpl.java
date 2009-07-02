package com.kesdip.player.components.flash.shockwaveflashobjects.impl;

import com.jniwrapper.*;
import com.jniwrapper.win32.*;
import com.jniwrapper.win32.automation.*;
import com.jniwrapper.win32.automation.impl.*;
import com.jniwrapper.win32.automation.types.*;
import com.jniwrapper.win32.com.*;
import com.jniwrapper.win32.com.impl.*;
import com.jniwrapper.win32.com.types.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.*;

/**
 * Represents COM interface IShockwaveFlash.
 */
public class IShockwaveFlashImpl extends IDispatchImpl
    implements IShockwaveFlash
{
    public static final String INTERFACE_IDENTIFIER = IShockwaveFlash.INTERFACE_IDENTIFIER;
    private static final IID _iid = IID.create(INTERFACE_IDENTIFIER);

    public IShockwaveFlashImpl()
    {
    }

    protected IShockwaveFlashImpl(IUnknownImpl that) throws ComException
    {
        super(that);
    }

    public IShockwaveFlashImpl(IUnknown that) throws ComException
    {
        super(that);
    }

    public IShockwaveFlashImpl(CLSID clsid, ClsCtx dwClsContext) throws ComException
    {
        super(clsid, dwClsContext);
    }

    public IShockwaveFlashImpl(CLSID clsid, IUnknownImpl pUnkOuter, ClsCtx dwClsContext) throws ComException
    {
        super(clsid, pUnkOuter, dwClsContext);
    }

    public Int32 getReadyState()
        throws ComException
    {
        Int32 pVal = new Int32();
       invokeStandardVirtualMethod(
            7,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public Int32 getTotalFrames()
        throws ComException
    {
        Int32 pVal = new Int32();
       invokeStandardVirtualMethod(
            8,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public VariantBool getPlaying()
        throws ComException
    {
        VariantBool pVal = new VariantBool();
       invokeStandardVirtualMethod(
            9,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setPlaying(
        VariantBool /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            10,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public Int getQuality()
        throws ComException
    {
        Int pVal = new Int();
       invokeStandardVirtualMethod(
            11,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setQuality(
        Int /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            12,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public Int getScaleMode()
        throws ComException
    {
        Int pVal = new Int();
       invokeStandardVirtualMethod(
            13,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setScaleMode(
        Int /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            14,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public Int getAlignMode()
        throws ComException
    {
        Int pVal = new Int();
       invokeStandardVirtualMethod(
            15,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setAlignMode(
        Int /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            16,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public Int32 getBackgroundColor()
        throws ComException
    {
        Int32 pVal = new Int32();
       invokeStandardVirtualMethod(
            17,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setBackgroundColor(
        Int32 /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            18,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public VariantBool getLoop()
        throws ComException
    {
        VariantBool pVal = new VariantBool();
       invokeStandardVirtualMethod(
            19,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setLoop(
        VariantBool /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            20,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public BStr getMovie()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            21,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setMovie(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            22,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public Int32 getFrameNum()
        throws ComException
    {
        Int32 pVal = new Int32();
       invokeStandardVirtualMethod(
            23,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setFrameNum(
        Int32 /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            24,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public void setZoomRect(
        Int32 /*[in]*/ left,
        Int32 /*[in]*/ top,
        Int32 /*[in]*/ right,
        Int32 /*[in]*/ bottom)
        throws ComException
    {
       invokeStandardVirtualMethod(
            25,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                left,
                top,
                right,
                bottom
            }
        );
    }

    public void zoom(
        Int /*[in]*/ factor)
        throws ComException
    {
       invokeStandardVirtualMethod(
            26,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                factor
            }
        );
    }

    public void pan(
        Int32 /*[in]*/ x,
        Int32 /*[in]*/ y,
        Int /*[in]*/ mode)
        throws ComException
    {
       invokeStandardVirtualMethod(
            27,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                x,
                y,
                mode
            }
        );
    }

    public void play()
        throws ComException
    {
       invokeStandardVirtualMethod(
            28,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[0]
        );
    }

    public void stop()
        throws ComException
    {
       invokeStandardVirtualMethod(
            29,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[0]
        );
    }

    public void back()
        throws ComException
    {
       invokeStandardVirtualMethod(
            30,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[0]
        );
    }

    public void forward()
        throws ComException
    {
       invokeStandardVirtualMethod(
            31,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[0]
        );
    }

    public void rewind()
        throws ComException
    {
       invokeStandardVirtualMethod(
            32,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[0]
        );
    }

    public void stopPlay()
        throws ComException
    {
       invokeStandardVirtualMethod(
            33,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[0]
        );
    }

    public void gotoFrame(
        Int32 /*[in]*/ FrameNum)
        throws ComException
    {
       invokeStandardVirtualMethod(
            34,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                FrameNum
            }
        );
    }

    public Int32 currentFrame()
        throws ComException
    {
        Int32 FrameNum = new Int32();
       invokeStandardVirtualMethod(
            35,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                FrameNum == null ? (Parameter)PTR_NULL : new Pointer(FrameNum)
            }
        );
        return FrameNum;
    }

    public VariantBool isPlaying()
        throws ComException
    {
        VariantBool Playing = new VariantBool();
       invokeStandardVirtualMethod(
            36,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                Playing == null ? (Parameter)PTR_NULL : new Pointer(Playing)
            }
        );
        return Playing;
    }

    public Int32 percentLoaded()
        throws ComException
    {
        Int32 percent = new Int32();
       invokeStandardVirtualMethod(
            37,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                percent == null ? (Parameter)PTR_NULL : new Pointer(percent)
            }
        );
        return percent;
    }

    public VariantBool frameLoaded(
        Int32 /*[in]*/ FrameNum)
        throws ComException
    {
        VariantBool loaded = new VariantBool();
       invokeStandardVirtualMethod(
            38,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                FrameNum,
                loaded == null ? (Parameter)PTR_NULL : new Pointer(loaded)
            }
        );
        return loaded;
    }

    public Int32 flashVersion()
        throws ComException
    {
        Int32 version = new Int32();
       invokeStandardVirtualMethod(
            39,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                version == null ? (Parameter)PTR_NULL : new Pointer(version)
            }
        );
        return version;
    }

    public BStr getWMode()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            40,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setWMode(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            41,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public BStr getSAlign()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            42,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setSAlign(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            43,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public VariantBool getMenu()
        throws ComException
    {
        VariantBool pVal = new VariantBool();
       invokeStandardVirtualMethod(
            44,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setMenu(
        VariantBool /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            45,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public BStr getBase()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            46,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setBase(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            47,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public BStr getScale()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            48,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setScale(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            49,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public VariantBool getDeviceFont()
        throws ComException
    {
        VariantBool pVal = new VariantBool();
       invokeStandardVirtualMethod(
            50,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setDeviceFont(
        VariantBool /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            51,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public VariantBool getEmbedMovie()
        throws ComException
    {
        VariantBool pVal = new VariantBool();
       invokeStandardVirtualMethod(
            52,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setEmbedMovie(
        VariantBool /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            53,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public BStr getBGColor()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            54,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setBGColor(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            55,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public BStr getQuality2()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            56,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setQuality2(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            57,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public void loadMovie(
        Int /*[in]*/ layer,
        BStr /*[in]*/ url)
        throws ComException
    {
       invokeStandardVirtualMethod(
            58,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                layer,
                url == null ? (Parameter)PTR_NULL : new Const(url)
            }
        );
    }

    public void TGotoFrame(
        BStr /*[in]*/ target,
        Int32 /*[in]*/ FrameNum)
        throws ComException
    {
       invokeStandardVirtualMethod(
            59,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                FrameNum
            }
        );
    }

    public void TGotoLabel(
        BStr /*[in]*/ target,
        BStr /*[in]*/ label)
        throws ComException
    {
       invokeStandardVirtualMethod(
            60,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                label == null ? (Parameter)PTR_NULL : new Const(label)
            }
        );
    }

    public Int32 TCurrentFrame(
        BStr /*[in]*/ target)
        throws ComException
    {
        Int32 FrameNum = new Int32();
       invokeStandardVirtualMethod(
            61,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                FrameNum == null ? (Parameter)PTR_NULL : new Pointer(FrameNum)
            }
        );
        return FrameNum;
    }

    public BStr TCurrentLabel(
        BStr /*[in]*/ target)
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            62,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void TPlay(
        BStr /*[in]*/ target)
        throws ComException
    {
       invokeStandardVirtualMethod(
            63,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target)
            }
        );
    }

    public void TStopPlay(
        BStr /*[in]*/ target)
        throws ComException
    {
       invokeStandardVirtualMethod(
            64,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target)
            }
        );
    }

    public void setVariable(
        BStr /*[in]*/ name,
        BStr /*[in]*/ value)
        throws ComException
    {
       invokeStandardVirtualMethod(
            65,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                name == null ? (Parameter)PTR_NULL : new Const(name),
                value == null ? (Parameter)PTR_NULL : new Const(value)
            }
        );
    }

    public BStr getVariable(
        BStr /*[in]*/ name)
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            66,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                name == null ? (Parameter)PTR_NULL : new Const(name),
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void TSetProperty(
        BStr /*[in]*/ target,
        Int /*[in]*/ property,
        BStr /*[in]*/ value)
        throws ComException
    {
       invokeStandardVirtualMethod(
            67,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                property,
                value == null ? (Parameter)PTR_NULL : new Const(value)
            }
        );
    }

    public BStr TGetProperty(
        BStr /*[in]*/ target,
        Int /*[in]*/ property)
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            68,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                property,
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void TCallFrame(
        BStr /*[in]*/ target,
        Int /*[in]*/ FrameNum)
        throws ComException
    {
       invokeStandardVirtualMethod(
            69,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                FrameNum
            }
        );
    }

    public void TCallLabel(
        BStr /*[in]*/ target,
        BStr /*[in]*/ label)
        throws ComException
    {
       invokeStandardVirtualMethod(
            70,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                label == null ? (Parameter)PTR_NULL : new Const(label)
            }
        );
    }

    public void TSetPropertyNum(
        BStr /*[in]*/ target,
        Int /*[in]*/ property,
        DoubleFloat /*[in]*/ value)
        throws ComException
    {
       invokeStandardVirtualMethod(
            71,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                property,
                value
            }
        );
    }

    public DoubleFloat TGetPropertyNum(
        BStr /*[in]*/ target,
        Int /*[in]*/ property)
        throws ComException
    {
        DoubleFloat pVal = new DoubleFloat();
       invokeStandardVirtualMethod(
            72,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                property,
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public DoubleFloat TGetPropertyAsNumber(
        BStr /*[in]*/ target,
        Int /*[in]*/ property)
        throws ComException
    {
        DoubleFloat pVal = new DoubleFloat();
       invokeStandardVirtualMethod(
            73,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                target == null ? (Parameter)PTR_NULL : new Const(target),
                property,
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public BStr getSWRemote()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            74,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setSWRemote(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            75,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public BStr getFlashVars()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            76,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setFlashVars(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            77,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public BStr getAllowScriptAccess()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            78,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setAllowScriptAccess(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            79,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public BStr getMovieData()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            80,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setMovieData(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            81,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public IUnknown getInlineData()
        throws ComException
    {
        IUnknownImpl ppIUnknown = new IUnknownImpl();
       invokeStandardVirtualMethod(
            82,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                ppIUnknown == null ? (Parameter)PTR_NULL : new Pointer((Parameter)ppIUnknown)
            }
        );
        return ppIUnknown;
    }

    public void setInlineData(
        IUnknown /*[in]*/ ppIUnknown)
        throws ComException
    {
       invokeStandardVirtualMethod(
            83,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                ppIUnknown == null ? (Parameter)PTR_NULL : new Const((Parameter)ppIUnknown)
            }
        );
    }

    public VariantBool getSeamlessTabbing()
        throws ComException
    {
        VariantBool pVal = new VariantBool();
       invokeStandardVirtualMethod(
            84,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setSeamlessTabbing(
        VariantBool /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            85,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public void enforceLocalSecurity()
        throws ComException
    {
       invokeStandardVirtualMethod(
            86,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[0]
        );
    }

    public VariantBool getProfile()
        throws ComException
    {
        VariantBool pVal = new VariantBool();
       invokeStandardVirtualMethod(
            87,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setProfile(
        VariantBool /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            88,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public BStr getProfileAddress()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            89,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setProfileAddress(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            90,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public Int32 getProfilePort()
        throws ComException
    {
        Int32 pVal = new Int32();
       invokeStandardVirtualMethod(
            91,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setProfilePort(
        Int32 /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            92,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal
            }
        );
    }

    public BStr callFunction(
        BStr /*[in]*/ request)
        throws ComException
    {
        BStr response = new BStr();
       invokeStandardVirtualMethod(
            93,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                request == null ? (Parameter)PTR_NULL : new Const(request),
                response == null ? (Parameter)PTR_NULL : new Pointer(response)
            }
        );
        return response;
    }

    public void setReturnValue(
        BStr /*[in]*/ returnValue)
        throws ComException
    {
       invokeStandardVirtualMethod(
            94,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                returnValue == null ? (Parameter)PTR_NULL : new Const(returnValue)
            }
        );
    }

    public void disableLocalSecurity()
        throws ComException
    {
       invokeStandardVirtualMethod(
            95,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[0]
        );
    }

    public BStr getAllowNetworking()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            96,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setAllowNetworking(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            97,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public BStr getAllowFullScreen()
        throws ComException
    {
        BStr pVal = new BStr();
       invokeStandardVirtualMethod(
            98,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Pointer(pVal)
            }
        );
        return pVal;
    }

    public void setAllowFullScreen(
        BStr /*[in]*/ pVal)
        throws ComException
    {
       invokeStandardVirtualMethod(
            99,
            Function.STDCALL_CALLING_CONVENTION,
            new Parameter[] {
                pVal == null ? (Parameter)PTR_NULL : new Const(pVal)
            }
        );
    }

    public IID getIID()
    {
        return _iid;
    }

    public Object clone()
    {
        return new IShockwaveFlashImpl(this);
    }
}
