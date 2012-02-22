/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.yank.api.handlers;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import java.lang.String;
import java.text.ParseException;
import java.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.*;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.yank.Yank;
import org.openmrs.module.yank.api.JsonUtil;
import org.openmrs.validator.PatientIdentifierValidator;

/**
 *
 * @author jkeiper
 */
public class PatientYankHandler implements YankHandler {

	protected final Log log = LogFactory.getLog(getClass());

	@Override
	public String summarize(String json) {
		if (json == null)
			return "nothing to summarize";
		
		Gson gson = new Gson();

		LinkedHashMap jPatient = gson.fromJson(json, LinkedHashMap.class);
		LinkedHashMap jPerson = (LinkedHashMap)jPatient.get("person");
		LinkedHashMap jName = (LinkedHashMap)jPerson.get("preferredName");
		List<LinkedHashMap> jIdentifiers = (List<LinkedHashMap>)jPatient.get("identifiers");

		List<String> idents = new ArrayList<String>();
		for (LinkedHashMap ident: jIdentifiers) {
			idents.add(JsonUtil.getStringOrNull(ident, "identifier"));
		}
		
		StringBuilder summary = new StringBuilder();
		
		summary.append(JsonUtil.getStringOrNull(jName, "givenName"))
				.append(" ")
				.append(JsonUtil.getStringOrNull(jName, "middleName"))
				.append(" ")
				.append(JsonUtil.getStringOrNull(jName, "familyName"))
				.append(" (")
				.append(Joiner.on(", ").join(idents))
				.append(") ")
				.append(JsonUtil.getStringOrNull(jPerson, "gender"))
				.append(", ")
				.append(JsonUtil.getNumberOrNull(jPerson, "age"))
				.append(" years");
		
		return summary.toString();
	}

	@Override
	public void process(Yank yank) {
		if (yank == null)
			throw new APIException("could not process null Yank");
		
		String json = yank.getData();
		if (StringUtils.isBlank(json))
			throw new APIException("no data for processing Yank#" + yank.getYankId());
		
		PersonService sPerson = Context.getPersonService();
		PatientService sPatient = Context.getPatientService();
		UserService sUser = Context.getUserService();
		
		// render top-level json object as a map
		Gson gson = new Gson();
		LinkedHashMap jPatient = gson.fromJson(yank.getData(), LinkedHashMap.class);

		// validate patient uuid is not in use
		String uuid = JsonUtil.getStringOrNull(jPatient, "uuid");
		if (uuid == null)
			throw new APIException("patient uuid is null");
		if (sPatient.getPatientByUuid(uuid) != null)
			throw new APIException("patient with uuid of " + uuid + " already exists in system");

		// get the main elements and collections from the json document
		LinkedHashMap jPerson = (LinkedHashMap)jPatient.get("person");
		List<LinkedHashMap> jNames = (List<LinkedHashMap>)jPerson.get("names");
		List<LinkedHashMap> jAddresses = (List<LinkedHashMap>)jPerson.get("addresses");
		List<LinkedHashMap> jAttributes = (List<LinkedHashMap>)jPerson.get("attributes");
		List<LinkedHashMap> jIdentifiers = (List<LinkedHashMap>)jPatient.get("identifiers");

		// build the patient
		Patient p = new Patient();
		
		AuditInfo ptAudit = null;
		AuditInfo pAudit = null;
		
		try {
			ptAudit = new AuditInfo(jPatient);
			pAudit = new AuditInfo(jPerson);
			p.setBirthdate(JsonUtil.getDateOrNull(jPerson, "birthdate"));
			p.setDeathDate(JsonUtil.getDateOrNull(jPerson, "deathDate"));
		} catch (ParseException ex) {
			throw new APIException("could not parse a date", ex);
		}
		
		// set top-level data
		p.setUuid(JsonUtil.getStringOrNull(jPatient, "uuid"));
		p.setGender(JsonUtil.getStringOrNull(jPerson, "gender"));
		p.setBirthdateEstimated(JsonUtil.getBooleanOrFalse(jPerson, "birthdateEstimated"));
		p.setDead(JsonUtil.getBooleanOrFalse(jPerson, "dead"));
		p.setVoided(JsonUtil.getBooleanOrFalse(jPerson, "voided"));
		// private Concept causeOfDeath;

		JsonUtil.applyAuditInfoTo(ptAudit, p);
		
		// cannot blindly apply audit info to the person of a patient ... bleh.
		if (pAudit != null) {
			p.setPersonCreator(sUser.getUserByUuid(pAudit.getCreator()));
			p.setPersonChangedBy(sUser.getUserByUuid(pAudit.getChangedBy()));
			p.setPersonVoidedBy(sUser.getUserByUuid(pAudit.getVoidedBy()));
			p.setPersonVoidReason(pAudit.getVoidReason());
			p.setPersonDateCreated(pAudit.getDateCreated());
			p.setPersonDateChanged(pAudit.getDateChanged());
			p.setPersonDateVoided(pAudit.getDateVoided());
		}

		// process names
		for (LinkedHashMap jName: jNames) {
			PersonName pName = new PersonName();
			pName.setPrefix(JsonUtil.getStringOrNull(jName, "prefix"));
			pName.setGivenName(JsonUtil.getStringOrNull(jName, "givenName"));
			pName.setMiddleName(JsonUtil.getStringOrNull(jName, "middleName"));
			pName.setFamilyNamePrefix(JsonUtil.getStringOrNull(jName, "familyNamePrefix"));
			pName.setFamilyName(JsonUtil.getStringOrNull(jName, "familyName"));
			pName.setFamilyName2(JsonUtil.getStringOrNull(jName, "familyName2"));
			pName.setFamilyNameSuffix(JsonUtil.getStringOrNull(jName, "familyNameSuffix"));
			pName.setDegree(JsonUtil.getStringOrNull(jName, "degree"));
			pName.setVoided(JsonUtil.getBooleanOrFalse(jName, "voided"));
			pName.setPreferred(JsonUtil.getBooleanOrFalse(jName, "preferred"));
			pName.setUuid(JsonUtil.getStringOrNull(jName, "uuid"));
			
			// TODO actually look up the person name objects with the link (more REST)
			JsonUtil.applyAuditInfoTo(pAudit, pName);
			
			p.addName(pName);
		}

		// process addresses
		for (LinkedHashMap jAddress: jAddresses) {
			PersonAddress pAddress = new PersonAddress();
			pAddress.setAddress1(JsonUtil.getStringOrNull(jAddress, "address1"));
			pAddress.setAddress2(JsonUtil.getStringOrNull(jAddress, "address2"));
			pAddress.setAddress3(JsonUtil.getStringOrNull(jAddress, "address3"));
			pAddress.setAddress4(JsonUtil.getStringOrNull(jAddress, "address4"));
			pAddress.setAddress5(JsonUtil.getStringOrNull(jAddress, "address5"));
			pAddress.setAddress6(JsonUtil.getStringOrNull(jAddress, "address6"));
			pAddress.setCityVillage(JsonUtil.getStringOrNull(jAddress, "cityVillage"));
			pAddress.setCountyDistrict(JsonUtil.getStringOrNull(jAddress, "countyDistrict"));
			pAddress.setStateProvince(JsonUtil.getStringOrNull(jAddress, "stateProvince"));
			pAddress.setCountry(JsonUtil.getStringOrNull(jAddress, "country"));
			pAddress.setPostalCode(JsonUtil.getStringOrNull(jAddress, "postalCode"));
			pAddress.setLatitude(JsonUtil.getStringOrNull(jAddress, "latitude"));
			pAddress.setLongitude(JsonUtil.getStringOrNull(jAddress, "longitude"));
			pAddress.setPreferred(JsonUtil.getBooleanOrFalse(jAddress, "preferred"));
			pAddress.setVoided(JsonUtil.getBooleanOrFalse(jAddress, "voided"));
			pAddress.setUuid(JsonUtil.getStringOrNull(jAddress, "uuid"));

			// TODO actually look up the address objects with the link (more REST)
			JsonUtil.applyAuditInfoTo(pAudit, pAddress);
			
			p.addAddress(pAddress);
		}
		
		// process person attributes
		for (LinkedHashMap jAttr: jAttributes) {
			// get the attribute type by uuid
			LinkedHashMap jAttrType = (LinkedHashMap)jAttr.get("attributeType");
			String attrTypeUuid = JsonUtil.getStringOrNull(jAttrType, "uuid");
			if (StringUtils.isBlank(attrTypeUuid))
				throw new APIException("PersonAttributeType uuid is blank.");
			PersonAttributeType pAttrType = sPerson.getPersonAttributeTypeByUuid(attrTypeUuid);
			if (pAttrType == null)
				throw new APIException("PersonAttributeType with uuid " + attrTypeUuid + " not found.");

			// build the attribute
			PersonAttribute pAttr = new PersonAttribute();
			pAttr.setAttributeType(pAttrType);
			pAttr.setValue(JsonUtil.getStringOrNull(jAttr, "value"));
			pAttr.setUuid(JsonUtil.getStringOrNull(jAttr, "uuid"));
			pAttr.setVoided(JsonUtil.getBooleanOrFalse(jAttr, "voided"));

			// TODO actually look up the person name objects with the link (more REST)
			JsonUtil.applyAuditInfoTo(pAudit, pAttr);
			
			p.addAttribute(pAttr);
		}

		// process patient identifiers
		for (LinkedHashMap jIdent: jIdentifiers) {
			// get the identifier type by uuid
			LinkedHashMap jIdentType = (LinkedHashMap)jIdent.get("identifierType");
			String identTypeUuid = JsonUtil.getStringOrNull(jIdentType, "uuid");
			if (StringUtils.isBlank(identTypeUuid))
				throw new APIException("PatientIdentifierType uuid is blank.");
			PatientIdentifierType pIdentType = sPatient.getPatientIdentifierTypeByUuid(identTypeUuid);
			if (pIdentType == null)
				throw new APIException("PatientIdentifierType with uuid " + identTypeUuid + " not found.");

			// get the location by uuid
			LinkedHashMap jLocation = (LinkedHashMap)jIdent.get("location");
			String locationUuid = JsonUtil.getStringOrNull(jLocation, "uuid");
			if (StringUtils.isBlank(locationUuid))
				throw new APIException("Location uuid is blank.");
			Location pIdentLoc = Context.getLocationService().getLocationByUuid(locationUuid);
			if (pIdentLoc == null)
				throw new APIException("Location with uuid " + locationUuid + " not found.");

			// build the identifier
			PatientIdentifier pIdentifier = new PatientIdentifier();
			pIdentifier.setIdentifierType(pIdentType);
			pIdentifier.setLocation(pIdentLoc);
			pIdentifier.setIdentifier(JsonUtil.getStringOrNull(jIdent, "identifier"));
			pIdentifier.setUuid(JsonUtil.getStringOrNull(jIdent, "uuid"));
			pIdentifier.setPreferred(JsonUtil.getBooleanOrFalse(jIdent, "preferred"));
			pIdentifier.setVoided(JsonUtil.getBooleanOrFalse(jIdent, "voided"));

			// TODO actually look up the person name objects with the link (more REST)
			JsonUtil.applyAuditInfoTo(ptAudit, pIdentifier);

//			// validate the identifier before adding it to the patient
//			try {
//				PatientIdentifierValidator.validateIdentifier(pIdentifier);
//			} catch(Exception ex) {
//				throw new APIException("Patient Identifier " + pIdentifier + " invalid.", ex);
//			}
			
			p.addIdentifier(pIdentifier);
		}

		// try to save the patient
		try {
			sPatient.savePatient(p);
		} catch (Exception ex) {
			throw new APIException("could not save patient from yank", ex);
		}
		
		// log the patient's creation event ... should only be used during debugging
		logPatient(p);
	}

	private void logPatient(Patient p) {
		// do some logging
		log.warn("created patient " + p.toString());
		log.warn("uuid = " + p.getUuid());
		log.warn("gender = " + p.getGender());
		log.warn("birthdate = " + p.getBirthdate());
		log.warn("bday est = " + p.getBirthdateEstimated());
		log.warn("dead = " + p.getDead());
		log.warn("deathdate = " + p.getDeathDate());
		log.warn("voided = " + p.getVoided());
		for (PersonName name: p.getNames())
			log.warn("name = " + name);
		for (PersonAddress addy: p.getAddresses())
			log.warn("address = " + addy);
		for (PersonAttribute attr: p.getAttributes())
			log.warn("attribute = " + attr);
		for (PatientIdentifier ident: p.getIdentifiers())
			log.warn("identifier = " + ident);
	}
	
}
