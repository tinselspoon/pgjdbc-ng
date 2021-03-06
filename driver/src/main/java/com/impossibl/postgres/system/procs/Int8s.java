/**
 * Copyright (c) 2013, impossibl.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of impossibl.com nor the names of its contributors may
 *    be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.impossibl.postgres.system.procs;

import com.impossibl.postgres.system.Context;
import com.impossibl.postgres.system.ConversionException;
import com.impossibl.postgres.types.Type;

import java.io.IOException;
import java.text.ParseException;

import io.netty.buffer.ByteBuf;

public class Int8s extends SimpleProcProvider {

  public Int8s() {
    super(new TxtEncoder(), new TxtDecoder(), new BinEncoder(), new BinDecoder(), "int8");
  }

  private static Long convertStringInput(Context context, String value) throws ConversionException {
    try {
      return context.getClientIntegerFormatter().parse(value).longValue();
    }
    catch (ParseException e) {
      throw new ConversionException("Invalid Long", e);
    }
  }

  private static String convertStringOutput(Context context, Number number) {
    return context.getClientIntegerFormatter().format(number);
  }

  static class BinDecoder extends NumericBinaryDecoder<Long> {

    BinDecoder() {
      super(8, Int8s::convertStringOutput);
    }

    @Override
    public Class<Long> getDefaultClass() {
      return Long.class;
    }

    @Override
    protected Long decodeNativeValue(Context context, Type type, Short typeLength, Integer typeModifier, ByteBuf buffer, Class<?> targetClass, Object targetContext) throws IOException {
      return buffer.readLong();
    }

  }

  static class BinEncoder extends NumericBinaryEncoder<Long> {

    BinEncoder() {
      super(8, Int8s::convertStringInput, val -> val ? (long) 1 : (long) 0, Number::longValue);
    }

    @Override
    public Class<Long> getDefaultClass() {
      return Long.class;
    }

    @Override
    protected void encodeNativeValue(Context context, Type type, Long value, Object sourceContext, ByteBuf buffer) throws IOException {
      buffer.writeLong(value);
    }

  }

  static class TxtDecoder extends NumericTextDecoder<Long> {

    TxtDecoder() {
      super(Int8s::convertStringOutput);
    }

    @Override
    public Class<Long> getDefaultClass() {
      return Long.class;
    }

    @Override
    protected Long decodeNativeValue(Context context, Type type, Short typeLength, Integer typeModifier, CharSequence buffer, Class<?> targetClass, Object targetContext) throws IOException, ParseException {
      return Long.valueOf(buffer.toString());
    }

  }

  static class TxtEncoder extends NumericTextEncoder<Long> {

    TxtEncoder() {
      super(Int8s::convertStringInput, val -> val ? (long) 1 : (long) 0, Number::longValue);
    }

    @Override
    public Class<Long> getDefaultClass() {
      return Long.class;
    }

    @Override
    protected void encodeNativeValue(Context context, Type type, Long value, Object sourceContext, StringBuilder buffer) throws IOException {
      buffer.append(value);
    }

  }

}
