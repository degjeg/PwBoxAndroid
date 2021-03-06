// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: myssl.proto at 9:1
package com.common.bean;

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

public final class GetPublicKeyRequest extends Message<GetPublicKeyRequest, GetPublicKeyRequest.Builder> {
  public static final ProtoAdapter<GetPublicKeyRequest> ADAPTER = new ProtoAdapter_GetPublicKeyRequest();

  private static final long serialVersionUID = 0L;

  public static final Integer DEFAULT_VER = 0;

  public static final ByteString DEFAULT_RANDOM1 = ByteString.EMPTY;

  public static final String DEFAULT_ACCOUNT = "";

  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  public final Integer ver;

  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#BYTES"
  )
  public final ByteString random1;

  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String account;

  public GetPublicKeyRequest(Integer ver, ByteString random1, String account) {
    this(ver, random1, account, ByteString.EMPTY);
  }

  public GetPublicKeyRequest(Integer ver, ByteString random1, String account, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.ver = ver;
    this.random1 = random1;
    this.account = account;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.ver = ver;
    builder.random1 = random1;
    builder.account = account;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof GetPublicKeyRequest)) return false;
    GetPublicKeyRequest o = (GetPublicKeyRequest) other;
    return unknownFields().equals(o.unknownFields())
        && Internal.equals(ver, o.ver)
        && Internal.equals(random1, o.random1)
        && Internal.equals(account, o.account);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (ver != null ? ver.hashCode() : 0);
      result = result * 37 + (random1 != null ? random1.hashCode() : 0);
      result = result * 37 + (account != null ? account.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (ver != null) builder.append(", ver=").append(ver);
    if (random1 != null) builder.append(", random1=").append(random1);
    if (account != null) builder.append(", account=").append(account);
    return builder.replace(0, 2, "GetPublicKeyRequest{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<GetPublicKeyRequest, Builder> {
    public Integer ver;

    public ByteString random1;

    public String account;

    public Builder() {
    }

    public Builder ver(Integer ver) {
      this.ver = ver;
      return this;
    }

    public Builder random1(ByteString random1) {
      this.random1 = random1;
      return this;
    }

    public Builder account(String account) {
      this.account = account;
      return this;
    }

    @Override
    public GetPublicKeyRequest build() {
      return new GetPublicKeyRequest(ver, random1, account, super.buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_GetPublicKeyRequest extends ProtoAdapter<GetPublicKeyRequest> {
    ProtoAdapter_GetPublicKeyRequest() {
      super(FieldEncoding.LENGTH_DELIMITED, GetPublicKeyRequest.class);
    }

    @Override
    public int encodedSize(GetPublicKeyRequest value) {
      return (value.ver != null ? ProtoAdapter.INT32.encodedSizeWithTag(1, value.ver) : 0)
          + (value.random1 != null ? ProtoAdapter.BYTES.encodedSizeWithTag(2, value.random1) : 0)
          + (value.account != null ? ProtoAdapter.STRING.encodedSizeWithTag(3, value.account) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, GetPublicKeyRequest value) throws IOException {
      if (value.ver != null) ProtoAdapter.INT32.encodeWithTag(writer, 1, value.ver);
      if (value.random1 != null) ProtoAdapter.BYTES.encodeWithTag(writer, 2, value.random1);
      if (value.account != null) ProtoAdapter.STRING.encodeWithTag(writer, 3, value.account);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public GetPublicKeyRequest decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.ver(ProtoAdapter.INT32.decode(reader)); break;
          case 2: builder.random1(ProtoAdapter.BYTES.decode(reader)); break;
          case 3: builder.account(ProtoAdapter.STRING.decode(reader)); break;
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
    public GetPublicKeyRequest redact(GetPublicKeyRequest value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
