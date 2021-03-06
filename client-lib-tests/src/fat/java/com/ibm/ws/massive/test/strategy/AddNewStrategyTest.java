/*******************************************************************************
 * Copyright (c) 2015 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.ibm.ws.massive.test.strategy;

import static com.ibm.ws.lars.testutils.ReflectionTricks.getAllResourcesWithDupes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import com.ibm.ws.massive.RepositoryBackendException;
import com.ibm.ws.massive.resources.AddNewStrategy;
import com.ibm.ws.massive.resources.MassiveResource.State;
import com.ibm.ws.massive.resources.RepositoryResourceException;
import com.ibm.ws.massive.resources.SampleResource;
import com.ibm.ws.massive.resources.UploadStrategy;

public class AddNewStrategyTest extends StrategyTestBaseClass {
    public AddNewStrategyTest() throws FileNotFoundException, IOException {
        super();
    }

    @Test
    public void testAddingToRepoUsingAddNewStrategyStrategy()
                    throws RepositoryBackendException, RepositoryResourceException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        _testRes.uploadToMassive(new AddNewStrategy());
        SampleResource readBack = SampleResource.getSample(_loginInfoEntry,
                                                           _testRes.get_id());
        assertTrue(
                   "The read back resource should be equivalent to the one we put in",
                   readBack.equivalent(_testRes));

        // Make sure there is a new asset uploaded (this strategy will
        // mean an upload will always create a new asset)
        _testRes.uploadToMassive(new AddNewStrategy());
        SampleResource readBack2 = SampleResource.getSample(_loginInfoEntry,
                                                            _testRes.get_id());
        assertTrue("The new asset should be equivalent to the one already added", readBack2.equivalent(readBack));
        assertFalse("The IDs of the assets should be different", readBack2.get_id() == readBack.get_id());
        assertEquals("There should now be two resource in the repo", 2, getAllResourcesWithDupes(_loginInfoEntry).size());

        _testRes.setFeaturedWeight("5");
        _testRes.uploadToMassive(new AddNewStrategy());
        SampleResource readBack3 = SampleResource.getSample(_loginInfoEntry,
                                                            _testRes.get_id());
        assertFalse("The new asset should not be equivalent to the one already added", readBack3.equivalent(readBack2));
        assertFalse("The IDs of the assets should be different", readBack3.get_id() == readBack2.get_id());
        assertEquals("There should now be three resource in the repo", 3, getAllResourcesWithDupes(_loginInfoEntry).size());
    }

    @Override
    protected UploadStrategy createStrategy(State ifMatching, State ifNoMatching) {
        return new AddNewStrategy(ifMatching, ifNoMatching);
    }

}
