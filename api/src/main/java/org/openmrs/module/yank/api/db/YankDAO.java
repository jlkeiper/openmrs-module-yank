/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.yank.api.db;

import java.util.List;
import org.openmrs.module.yank.Yank;
import org.openmrs.module.yank.YankArchive;
import org.openmrs.module.yank.YankError;
import org.openmrs.module.yank.api.YankService;

/**
 *  Database methods for {@link YankService}.
 */
public interface YankDAO {

	public Yank saveYank(Yank yank);

	public List<Yank> getAllYanks();

	public Yank getYank(Integer id);

	public YankError saveYankError(YankError error);

	public void purgeYank(Yank yank);

	public YankArchive saveYankArchive(YankArchive archive);

	public List<Yank> getYankBatch(int start, int length, String query);

	public Integer countYanks(String query);

	public List<Integer> getAllYankIds();
	
}