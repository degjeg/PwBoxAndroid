// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: pwbox.proto at 73:1
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

public final class GetProtectRes extends Message<GetProtectRes, GetProtectRes.Builder> {
  public static final ProtoAdapter<GetProtectRes> ADAPTER = new ProtoAdapter_GetProtectRes();

  private static final long serialVersionUID = 0L;

  public static final Integer DEFAULT_RETCODE = 0;

  public static final String DEFAULT_ERRORMESSAGE = "";

  public static final String DEFAULT_QUESTION = "";

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
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String question;

  public GetProtectRes(Integer retCode, String errorMessage, String question) {
    this(retCode, errorMessage, question, ByteString.EMPTY);
  }

  public GetProtectRes(Integer retCode, String errorMessage, String question, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.retCode = retCode;
    this.errorMessage = errorMessage;
    this.question = question;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.retCode = retCode;
    builder.errorMessage = errorMessage;
    builder.question = question;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof GetProtectRes)) return false;
    GetProtectRes o = (GetProtectRes) other;
    return unknownFields().equals(o.unknownFields())
        && Internal.equals(retCode, o.retCode)
        && Internal.equals(errorMessage, o.errorMessage)
        && Internal.equals(question, o.question);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (retCode != null ? retCode.hashCode() : 0);
      result = result * 37 + (errorMessage != null ? errorMessage.hashCode() : 0);
      result = result * 37 + (question != null ? question.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (retCode != null) builder.append(", retCode=").append(retCode);
    if (errorMessage != null) builder.append(", errorMessage=").append(errorMessage);
    if (question != null) builder.append(", question=").append(question);
    return builder.replace(0, 2, "GetProtectRes{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<GetProtectRes, Builder> {
    public Integer retCode;

    public String errorMessage;

    public String question;

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

    public Builder question(String question) {
      this.question = question;
      return this;
    }

    @Override
    public GetProtectRes build() {
      return new GetProtectRes(retCode, errorMessage, question, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_GetProtectRes extends ProtoAdapter<GetProtectRes> {
    ProtoAdapter_GetProtectRes() {
      super(FieldEncoding.LENGTH_DELIMITED, GetProtectRes.class);
    }

    @Override
    public int encodedSize(GetProtectRes value) {
      return (value.retCode != null ? ProtoAdapter.INT32.encodedSizeWithTag(1, value.retCode) : 0)
          + (value.errorMessage != null ? ProtoAdapter.STRING.encodedSizeWithTag(2, value.errorMessage) : 0)
          + (value.question != null ? ProtoAdapter.STRING.encodedSizeWithTag(3, value.question) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, GetProtectRes value) throws IOException {
      if (value.retCode != null) ProtoAdapter.INT32.encodeWithTag(writer, 1, value.retCode);
      if (value.errorMessage != null) ProtoAdapter.STRING.encodeWithTag(writer, 2, value.errorMessage);
      if (value.question != null) ProtoAdapter.STRING.encodeWithTag(writer, 3, value.question);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public GetProtectRes decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.retCode(ProtoAdapter.INT32.decode(reader)); break;
          case 2: builder.errorMessage(ProtoAdapter.STRING.decode(reader)); break;
          case 3: builder.question(ProtoAdapter.STRING.decode(reader)); break;
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
    public GetProtectRes redact(GetProtectRes value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}