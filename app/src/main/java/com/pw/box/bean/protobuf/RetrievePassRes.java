// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: pwbox.proto at 102:1
package com.pw.box.bean.protobuf;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;
import java.io.IOException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import okio.ByteString;

public final class RetrievePassRes extends Message<RetrievePassRes, RetrievePassRes.Builder> {
  public static final ProtoAdapter<RetrievePassRes> ADAPTER = new ProtoAdapter_RetrievePassRes();

  private static final long serialVersionUID = 0L;

  public static final Integer DEFAULT_RETCODE = 0;

  public static final String DEFAULT_ERRORMESSAGE = "";

  public static final ByteString DEFAULT_RAW_KEY_BY_ANSWER = ByteString.EMPTY;

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  public final Integer retCode;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String errorMessage;

  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#BYTES"
  )
  public final ByteString raw_key_by_answer;

  public RetrievePassRes(Integer retCode, String errorMessage, ByteString raw_key_by_answer) {
    this(retCode, errorMessage, raw_key_by_answer, ByteString.EMPTY);
  }

  public RetrievePassRes(Integer retCode, String errorMessage, ByteString raw_key_by_answer, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.retCode = retCode;
    this.errorMessage = errorMessage;
    this.raw_key_by_answer = raw_key_by_answer;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.retCode = retCode;
    builder.errorMessage = errorMessage;
    builder.raw_key_by_answer = raw_key_by_answer;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof RetrievePassRes)) return false;
    RetrievePassRes o = (RetrievePassRes) other;
    return unknownFields().equals(o.unknownFields())
        && Internal.equals(retCode, o.retCode)
        && Internal.equals(errorMessage, o.errorMessage)
        && Internal.equals(raw_key_by_answer, o.raw_key_by_answer);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (retCode != null ? retCode.hashCode() : 0);
      result = result * 37 + (errorMessage != null ? errorMessage.hashCode() : 0);
      result = result * 37 + (raw_key_by_answer != null ? raw_key_by_answer.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (retCode != null) builder.append(", retCode=").append(retCode);
    if (errorMessage != null) builder.append(", errorMessage=").append(errorMessage);
    if (raw_key_by_answer != null) builder.append(", raw_key_by_answer=").append(raw_key_by_answer);
    return builder.replace(0, 2, "RetrievePassRes{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<RetrievePassRes, Builder> {
    public Integer retCode;

    public String errorMessage;

    public ByteString raw_key_by_answer;

    public Builder() {
    }

    public Builder retCode(Integer retCode) {
      this.retCode = retCode;
      return this;
    }

    public Builder errorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
      return this;
    }

    public Builder raw_key_by_answer(ByteString raw_key_by_answer) {
      this.raw_key_by_answer = raw_key_by_answer;
      return this;
    }

    @Override
    public RetrievePassRes build() {
      return new RetrievePassRes(retCode, errorMessage, raw_key_by_answer, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_RetrievePassRes extends ProtoAdapter<RetrievePassRes> {
    ProtoAdapter_RetrievePassRes() {
      super(FieldEncoding.LENGTH_DELIMITED, RetrievePassRes.class);
    }

    @Override
    public int encodedSize(RetrievePassRes value) {
      return (value.retCode != null ? ProtoAdapter.INT32.encodedSizeWithTag(1, value.retCode) : 0)
          + (value.errorMessage != null ? ProtoAdapter.STRING.encodedSizeWithTag(2, value.errorMessage) : 0)
          + (value.raw_key_by_answer != null ? ProtoAdapter.BYTES.encodedSizeWithTag(3, value.raw_key_by_answer) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, RetrievePassRes value) throws IOException {
      if (value.retCode != null) ProtoAdapter.INT32.encodeWithTag(writer, 1, value.retCode);
      if (value.errorMessage != null) ProtoAdapter.STRING.encodeWithTag(writer, 2, value.errorMessage);
      if (value.raw_key_by_answer != null) ProtoAdapter.BYTES.encodeWithTag(writer, 3, value.raw_key_by_answer);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public RetrievePassRes decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.retCode(ProtoAdapter.INT32.decode(reader)); break;
          case 2: builder.errorMessage(ProtoAdapter.STRING.decode(reader)); break;
          case 3: builder.raw_key_by_answer(ProtoAdapter.BYTES.decode(reader)); break;
          default: {
            FieldEncoding fieldEncoding = reader.peekFieldEncoding();
            Object value = fieldEncoding.rawProtoAdapter().decode(reader);
            builder.addUnknownField(tag, fieldEncoding, value);
          }
        }
      }
      reader.endMessage(token);
      return builder.build();
    }

    @Override
    public RetrievePassRes redact(RetrievePassRes value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
