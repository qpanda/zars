package net.soomsam.zirmegghuette.zars.web;

import net.thucydides.core.annotations.Feature;

public class Zars {
	// https://github.com/thucydides-webtests/thucydides/wiki/Thucydides-Users-Manual
	
	@Feature
	public class Login {
		public class TestLogin {}
	}
	
	@Feature
    public class Search {
        public class SearchByKeyword {}
        public class SearchForQuote{}
    }

    @Feature
    public class Contribute {
        public class AddNewArticle {}
        public class EditExistingArticle {}
    }
}
