package com.kesdip.player.components.flash.shockwaveflashobjects.server;

import com.jniwrapper.*;
import com.jniwrapper.win32.com.*;
import com.jniwrapper.win32.com.impl.*;
import com.jniwrapper.win32.com.server.*;
import com.jniwrapper.win32.com.types.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.*;
import com.kesdip.player.components.flash.shockwaveflashobjects.impl.*;

/**
 * Represents VTBL for COM interface IFlashFactory.
 */
public class IFlashFactoryVTBL extends IUnknownVTBL
{
    public IFlashFactoryVTBL(CoClassMetaInfo classMetaInfo)
    {
        super(classMetaInfo);

        addMembers(
            new VirtualMethodCallback[] {
            }
        );
    }
}