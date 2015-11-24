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

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Objects;

/**
 * A {@code ClassAttributePK} defines the primary key for an instance of {@link ClassAttribute}.
 * @author Chris Phillipson
 */
@Immutable
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

    protected ClassAttributePK() {}
    
    public ClassAttributePK(Integer cid, Integer aid, Integer tid) {
        this.cid = cid;
        this.aid = aid;
        this.tid = tid;
    }
    
    public Integer getCid() {
        return cid;
    }

    public Integer getAid() {
        return aid;
    }

    public Integer getTid() {
        return tid;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cid, aid, tid);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ClassAttributePK) {
            ClassAttributePK that = (ClassAttributePK) object;
            return 
                    Objects.equal(this.cid, that.cid) 
                    && Objects.equal(this.aid, that.aid)
                    && Objects.equal(this.tid, that.tid);
        }
        return false;
    }

}
