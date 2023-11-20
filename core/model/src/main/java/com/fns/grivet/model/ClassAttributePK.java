/*
 * Copyright 2015 - Chris Phillipson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fns.grivet.model;

import java.io.Serializable;

/**
 * A {@code ClassAttributePK} defines the primary key for an instance of {@link ClassAttribute}.
 * @author Chris Phillipson
 */
public class ClassAttributePK implements Serializable {

    /**
     * Version number used during deserialization to verify that the sender and receiver
     * of this serialized object have loaded classes for this object that
     * are compatible with respect to serialization.
     */
    private static final long serialVersionUID = 1L;

    private Integer cid;
    private Integer aid;
    private Integer tid;

    public Integer getCid() {
        return this.cid;
    }

    public Integer getAid() {
        return this.aid;
    }

    public Integer getTid() {
        return this.tid;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ClassAttributePK)) return false;
        final ClassAttributePK other = (ClassAttributePK) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$cid = this.getCid();
        final Object other$cid = other.getCid();
        if (this$cid == null ? other$cid != null : !this$cid.equals(other$cid)) return false;
        final Object this$aid = this.getAid();
        final Object other$aid = other.getAid();
        if (this$aid == null ? other$aid != null : !this$aid.equals(other$aid)) return false;
        final Object this$tid = this.getTid();
        final Object other$tid = other.getTid();
        if (this$tid == null ? other$tid != null : !this$tid.equals(other$tid)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ClassAttributePK;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $cid = this.getCid();
        result = result * PRIME + ($cid == null ? 43 : $cid.hashCode());
        final Object $aid = this.getAid();
        result = result * PRIME + ($aid == null ? 43 : $aid.hashCode());
        final Object $tid = this.getTid();
        result = result * PRIME + ($tid == null ? 43 : $tid.hashCode());
        return result;
    }

    public ClassAttributePK(final Integer cid, final Integer aid, final Integer tid) {
        this.cid = cid;
        this.aid = aid;
        this.tid = tid;
    }

    protected ClassAttributePK() {}
}
