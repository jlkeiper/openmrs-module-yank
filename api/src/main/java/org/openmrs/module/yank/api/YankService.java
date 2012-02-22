/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.yank.api;

import java.util.List;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.yank.Yank;
import org.openmrs.module.yank.YankArchive;
import org.openmrs.module.yank.YankError;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service exposes module's core functionality. It is a Spring managed bean
 * which is configured in moduleApplicationContext.xml. <p> It can be accessed
 * only via Context:<br>
 * <code>
 * Context.getService(YankService.class).someMethod();
 * </code>
 *
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface YankService extends OpenmrsService {

	public String yankFromServer(String server, String username, String password, String type, String uuid);

	public Yank saveYank(Yank yank);

	public YankError saveYankError(YankError error);

	public YankArchive saveYankArchive(YankArchive archive);

	public List<Yank> getAllYanks();

	public Yank getYank(Integer id);
	
	@Transactional(noRollbackFor=Throwable.class)
	public Boolean processYank(Yank yank);

	public void moveYankToErrors(Yank yank, Throwable ex);

	public void purgeYank(Yank yank);

	public void moveYankToArchives(Yank yank);

	public String getSummaryForType(String datatype, String data);

	public List<Yank> getYankBatch(int start, int length, String query);

	public Integer countYanks(String query);

	public List<Integer> getAllYankIds();

}