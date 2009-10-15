package com.kesdip.player.components.flash.shockwaveflashobjects;

import com.jniwrapper.*;
import com.jniwrapper.win32.com.*;
import com.jniwrapper.win32.com.impl.*;
import com.jniwrapper.win32.com.types.*;
import com.jniwrapper.win32.ole.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.impl.*;

/**
 * Represents COM coclass ShockwaveFlash.
 */
public class ShockwaveFlash extends CoClass
{
    public static final CLSID CLASS_ID = CLSID.create("{D27CDB6E-AE6D-11CF-96B8-444553540000}");

    public ShockwaveFlash()
    {
    }

    public ShockwaveFlash(ShockwaveFlash that)
    {
        super(that);
    }

    /**
     * Creates coclass and returns its default interface.
     */
    public static IShockwaveFlash create(ClsCtx dwClsContext) throws ComException
    {
        final IShockwaveFlashImpl intf = new IShockwaveFlashImpl(CLASS_ID, dwClsContext);
        OleFunctions.oleRun(intf);
        return intf;
    }

    /**
     * Queries the <code>IShockwaveFlash</code> interface from IUnknown instance.
     */
    public static IShockwaveFlash queryInterface(IUnknown unknown) throws ComException
    {
        final IShockwaveFlashImpl result = new IShockwaveFlashImpl();
        unknown.queryInterface(result.getIID(), result);
        return result;
    }

    public CLSID getCLSID()
    {
        return CLASS_ID;
    }

    public Object clone()
    {
        return new ShockwaveFlash(this);
    }
}